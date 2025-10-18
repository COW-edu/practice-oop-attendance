package attendance.application;

import attendance.application.dto.command.FixCommand;
import attendance.application.dto.view.HistoryView;
import attendance.application.dto.view.RegisterView;
import attendance.application.dto.view.RiskView;
import attendance.application.dto.view.RiskViewRow;
import attendance.application.error.NoSchoolDayError;
import attendance.application.error.UnRegisterNameError;
import attendance.domain.model.AttendanceRecord;
import attendance.domain.model.CrewAttendance;
import attendance.domain.model.RiskLevel;
import attendance.domain.model.Status;
import attendance.domain.policy.AbsentPolicy;
import attendance.domain.policy.LateRule;
import attendance.domain.policy.RiskPolicy;
import attendance.domain.policy.SchoolDayPolicy;
import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AttendanceService {

  private final AttendanceRepository repository;
  private final LateRule lateRule;
  private final SchoolDayPolicy schoolDay;
  private final AbsentPolicy absence;
  private final RiskPolicy riskPolicy;
  private final Set<String> registeredNicknames;

  public AttendanceService(AttendanceRepository repository,
      LateRule lateRule,
      SchoolDayPolicy schoolDay,
      AbsentPolicy absence,
      RiskPolicy riskPolicy,
      Set<String> registeredNicknames) {
    this.repository = Objects.requireNonNull(repository);
    this.lateRule = Objects.requireNonNull(lateRule);
    this.schoolDay = Objects.requireNonNull(schoolDay);
    this.absence = Objects.requireNonNull(absence);
    this.riskPolicy = Objects.requireNonNull(riskPolicy);
    this.registeredNicknames = Objects.requireNonNull(registeredNicknames);
  }

  public RegisterView register(Nickname nickname, ZonedDateTime when) {
    requireRegistered(nickname);
    var date = new AttendanceDate(when.toLocalDate());
    if (!schoolDay.isSchoolDay(date.getValue())) {
      throw new NoSchoolDayError(koreanDate(date.getValue()));
    }

    var sheet = repository.findByNickname(nickname).orElseGet(() -> CrewAttendance.empty(nickname));
    if (sheet.records().containsKey(date)) {
      return RegisterView.duplicate(nickname, date);
    }

    var saved = repository.save(sheet.register(date, AttendanceTime.of(when.toLocalTime())));
    repository.save(saved);
    return RegisterView.registered(saved.nickname(), date);
  }

  public AttendanceRecord fix(FixCommand cmd) {
    requireRegistered(cmd.nickname());
    var sheet = repository.findByNickname(cmd.nickname())
        .orElseThrow(() -> new IllegalArgumentException("해당 크루의 출석부가 없습니다."));
    var before = sheet.records().get(cmd.date());
    if (before == null) {
      throw new IllegalArgumentException("해당 날짜의 기록이 없습니다.");
    }

    var afterSheet = repository.save(sheet.fix(cmd.date(), cmd.time()));
    return afterSheet.records().get(cmd.date());
  }

  public HistoryView history(Nickname nickname, AttendanceDate until) {
    requireRegistered(nickname);
    var sheet = repository.findByNickname(nickname).orElseGet(() -> CrewAttendance.empty(nickname));
    var filled = sheet.fillUntil(until, absence);
    return HistoryView.from(filled, sheet.nickname(), lateRule);
  }


  public Optional<AttendanceRecord> findRecord(Nickname nickname, AttendanceDate date) {
    var sheet = repository.findByNickname(nickname).orElse(null);
    if (sheet == null) {
      return Optional.empty();
    }
    var rec = sheet.records().get(date);
    if (rec == null) {
      return Optional.empty();
    }
    return Optional.of(rec);
  }

  public RiskView riskView(AttendanceDate until) {
    var rows = registeredNicknames.stream()
        .map(Nickname::new)
        .map(nickname -> toRiskRow(nickname, until))
        .filter(Objects::nonNull)
        .sorted(riskOrder())
        .toList();
    return RiskView.of(rows);
  }

  private RiskViewRow toRiskRow(Nickname nickname, AttendanceDate until) {
    var sheet = repository.findByNickname(nickname).orElseGet(() -> CrewAttendance.empty(nickname));
    Map<AttendanceDate, AttendanceRecord> filled = sheet.fillUntil(until, absence);
    var counts = countByStatus(filled);
    var absent = counts.getOrDefault(Status.ABSENT, 0);
    var late = counts.getOrDefault(Status.LATE, 0);

    var level = riskPolicy.level(absent, late);
    if (level == RiskLevel.NONE) {
      return null;
    }
    return new RiskViewRow(nickname, absent, late, level);
  }

  private Map<Status, Integer> countByStatus(Map<AttendanceDate, AttendanceRecord> filled) {
    return filled.values().stream()
        .collect(Collectors.groupingBy(
            r -> r.status(lateRule),
            () -> new EnumMap<>(Status.class),
            Collectors.summingInt(r -> 1)
        ));
  }

  private void requireRegistered(Nickname nickname) {
    if (registeredNicknames.contains(nickname.getValue())) {
      return;
    }
    throw new UnRegisterNameError();
  }

  private static String koreanDate(LocalDate d) {
    String[] DOW = {"월", "화", "수", "목", "금", "토", "일"};
    return d.getMonthValue() + "월 " + d.getDayOfMonth() + "일 " + DOW[d.getDayOfWeek().getValue()
        - 1] + "요일";
  }

  private static Comparator<RiskViewRow> riskOrder() {
    var groupRank = Map.of(
        RiskLevel.FIRE, 0,
        RiskLevel.TALK, 1,
        RiskLevel.WARNING, 2,
        RiskLevel.NONE, 3
    );

    return Comparator
        .comparingInt((RiskViewRow r) -> groupRank.get(r.level()))
        .thenComparing(Comparator.comparingInt(RiskViewRow::absentCount).reversed())
        .thenComparing(Comparator.comparingInt(RiskViewRow::lateCount).reversed())
        .thenComparing(r -> r.nickname().getValue());
  }
}
