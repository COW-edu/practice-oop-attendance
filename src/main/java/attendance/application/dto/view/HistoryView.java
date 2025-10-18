package attendance.application.dto.view;

import attendance.domain.model.AttendanceRecord;
import attendance.domain.model.Status;
import attendance.domain.policy.LateRule;
import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record HistoryView(Nickname nickname, List<Line> lines, int onTimeCount, int lateCount,
                          int absentCount) {

  public static HistoryView of(Nickname nickname, List<Line> lines) {
    var copy = List.copyOf(lines);
    int present = (int) copy.stream().filter(l -> l.status() == Status.ON_TIME).count();
    int late = (int) copy.stream().filter(l -> l.status() == Status.LATE).count();
    int absent = (int) copy.stream().filter(l -> l.status() == Status.ABSENT).count();
    return new HistoryView(nickname, copy, present, late, absent);
  }

  public static HistoryView from(Map<AttendanceDate, AttendanceRecord> filled, Nickname nickname,
      LateRule rule) {
    var lines = filled.entrySet().stream()
        .sorted(Entry.comparingByKey(Comparator.naturalOrder()))
        .map(e -> Line.of(e.getValue(), rule))
        .toList();
    return of(nickname, lines);
  }


  public record Line(AttendanceDate date, AttendanceTime time, Status status) {

    private static final String[] DOW = {"월", "화", "수", "목", "금", "토", "일"};

    public static Line of(AttendanceRecord r, LateRule rule) {
      return new Line(r.date(), r.time(), r.status(rule));
    }

    private static void appendTime(StringBuilder sb, AttendanceTime t) {
      if (t.isAbsent()) {
        sb.append("--:-- ");
        return;
      }
      var v = t.value();
      two(sb, v.getHour());
      sb.append(":");
      two(sb, v.getMinute());
      sb.append(" ");
    }

    private static void appendStatus(StringBuilder sb, Status s) {
      if (s == Status.ON_TIME) {
        sb.append("(출석)");
        return;
      }
      if (s == Status.LATE) {
        sb.append("(지각)");
        return;
      }
      sb.append("(결석)");
    }

    private static void two(StringBuilder sb, int n) {
      if (n < 10) {
        sb.append('0');
        sb.append(n);
        return;
      }
      sb.append(n);
    }
  }
}


