package attendance;

import attendance.application.AttendanceService;
import attendance.infrastructure.InMemoryAttendanceRepository;
import attendance.domain.policy.AbsentPolicy;
import attendance.domain.policy.LateRule;
import attendance.domain.policy.RiskPolicy;
import attendance.domain.policy.SchoolDayPolicy;
import attendance.infrastructure.CsvBootstrapper;
import attendance.presentation.ConsoleApp;
import attendance.presentation.InputView;
import attendance.presentation.ResultView;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Application {

  public static void main(String[] args) {
    var zone = ZoneId.of("Asia/Seoul");

    var repo = new InMemoryAttendanceRepository();
    Set<String> members = CsvBootstrapper.load(repo,
        Paths.get("src/main/resources/attendances.csv"));

    var lateRule = createLateRule();
    var schoolDay = new SchoolDayPolicy();
    var absent = new AbsentPolicy();
    var riskPolicy = new RiskPolicy(3, 2, 3, 5);

    var service = new AttendanceService(repo, lateRule, schoolDay, absent, riskPolicy, members);
    var input = new InputView(System.in);
    var result = new ResultView(System.out, lateRule);
    var app = new ConsoleApp(input, result, service, zone);

    app.run();
  }

  private static LateRule createLateRule() {
    Map<DayOfWeek, LocalTime> start = new EnumMap<>(DayOfWeek.class);
    start.put(DayOfWeek.MONDAY, LocalTime.of(13, 0));
    Stream.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
        .forEach(d -> {
          start.put(d, LocalTime.of(10, 0));
        });

    return new LateRule(
        start,
        Duration.ofMinutes(5),   // 지각 5분까지
        Duration.ofMinutes(30)   // 결석 30분까지
    );
  }
}
