package attendance.domain.policy;

import attendance.domain.model.Status;
import attendance.domain.value.AttendanceTime;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;

public final class LateRule {

  private final Map<DayOfWeek, LocalTime> startTime;
  private final Duration lateThreshold;
  private final Duration absentThreshold;

  public LateRule(Map<DayOfWeek, LocalTime> startTimes,
      Duration lateThreshold,
      Duration absentThreshold) {
    this.startTime = Map.copyOf(startTimes);
    this.lateThreshold = Objects.requireNonNull(lateThreshold);
    this.absentThreshold = Objects.requireNonNull(absentThreshold);
  }

  public Status judge(LocalDate date, AttendanceTime time) {
    if (time.isAbsent()) {
      return Status.ABSENT;
    }
    var start = startTime.get(date.getDayOfWeek());
    var diff = Duration.between(start, time.value());
    if (diff.isNegative() || diff.isZero()) {
      return Status.ON_TIME;
    }
    if (diff.compareTo(lateThreshold) <= 0) {
      return Status.ON_TIME;
    }
    if (diff.compareTo(absentThreshold) <= 0) {
      return Status.LATE;
    }
    return Status.ABSENT;
  }
}
