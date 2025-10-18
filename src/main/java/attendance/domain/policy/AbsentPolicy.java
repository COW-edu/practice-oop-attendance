package attendance.domain.policy;

import attendance.domain.model.AttendanceRecord;
import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AbsentPolicy {

  public Map<AttendanceDate, AttendanceRecord> fill(
      Map<AttendanceDate, AttendanceRecord> existing, AttendanceDate until
  ) {
    if (existing.isEmpty()) {
      return existing;
    }

    var owner = existing.values().iterator().next().nickname();
    var start = existing.keySet().stream()
        .map(AttendanceDate::getValue)
        .min(LocalDate::compareTo)
        .orElseThrow();

    return Stream.iterate(start, d -> !d.isAfter(until.getValue()), d -> d.plusDays(1))
        .filter(AbsentPolicy::isSchoolDay) // ✅ 주말 제외(평일만 채움)
        .map(d -> {
          var key = new AttendanceDate(d);
          return existing.getOrDefault(key,
              AttendanceRecord.of(owner, key, AttendanceTime.absent()));
        })
        .collect(Collectors.toMap(
            AttendanceRecord::date, r -> r, (a, b) -> a, TreeMap::new
        ));
  }

  private static boolean isSchoolDay(LocalDate d) {
    int v = d.getDayOfWeek().getValue();
    return v >= 1 && v <= 5;
  }
}
