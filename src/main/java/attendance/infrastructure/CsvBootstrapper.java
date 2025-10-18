package attendance.infrastructure;

import attendance.application.AttendanceRepository;
import attendance.domain.model.CrewAttendance;
import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CsvBootstrapper {

  private static final DateTimeFormatter INIT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public static Set<String> load(AttendanceRepository repo, Path csvPath) {
    var lines = readAll(csvPath);
    if (lines.isEmpty()) {
      return Set.of();
    }

    return lines.stream()
        .map(CsvBootstrapper::stripBom)
        .skip(1)
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(line -> apply(line, repo))
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private static List<String> readAll(Path path) {
    try {
      return Files.readAllLines(path);
    } catch (Exception e) {
      return List.of();
    }
  }

  private static String apply(String line, AttendanceRepository repo) {
    var t = line.split(",");
    if (t.length < 2) {
      return null;
    }

    var nickname = new Nickname(t[0].trim());
    var dt = LocalDateTime.parse(t[1].trim(), INIT_FMT);
    var date = new AttendanceDate(dt.toLocalDate());

    var sheet = repo.findByNickname(nickname).orElseGet(() -> CrewAttendance.empty(nickname));

    if (!sheet.records().containsKey(date)) {
      sheet = sheet.register(date, AttendanceTime.of(dt.toLocalTime()));
    }
    repo.save(sheet);
    return nickname.getValue();
  }

  private static String stripBom(String s) {
    if (!s.isEmpty() && s.charAt(0) == '\uFEFF') {
      return s.substring(1);
    }
    return s;
  }

  private CsvBootstrapper() {
  }
}
