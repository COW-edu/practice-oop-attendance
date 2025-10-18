package attendance.domain.model;

import attendance.domain.policy.AbsentPolicy;
import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public final class CrewAttendance {

  private final Nickname nickname;
  private final Map<AttendanceDate, AttendanceRecord> records;

  private CrewAttendance(Nickname nickname, Map<AttendanceDate, AttendanceRecord> records) {
    this.nickname = nickname;
    this.records = new TreeMap<>(records);
  }

  public static CrewAttendance empty(Nickname nickname) {
    return new CrewAttendance(nickname, Map.of());
  }

  public CrewAttendance register(AttendanceDate date, AttendanceTime time) {
    requireNoDuplicate(date);
    var next = copy(records);
    next.put(date, AttendanceRecord.of(nickname, date, time));
    return new CrewAttendance(nickname, next);
  }

  public CrewAttendance fix(AttendanceDate date, AttendanceTime newTime) {
    var before = getRequired(date);
    var next = copy(records);
    next.put(date, before.withTime(newTime));
    return new CrewAttendance(nickname, next);
  }

  public Map<AttendanceDate, AttendanceRecord> fillUntil(AttendanceDate until,
      AbsentPolicy policy) {
    return policy.fill(records, until);
  }

  public Nickname nickname() {
    return nickname;
  }
  public Map<AttendanceDate, AttendanceRecord> records() {
    return records;
  }
  private void requireNoDuplicate(AttendanceDate date) {
    if (records.containsKey(date)) {
      throw new IllegalStateException("이미 해당 날짜에 기록이 있습니다.");
    }
  }

  private AttendanceRecord getRequired(AttendanceDate date) {
    var r = records.get(date);
    if (r == null) {
      throw new IllegalArgumentException("해당 날짜의 기록이 없습니다.");
    }
    return r;
  }

  private static Map<AttendanceDate, AttendanceRecord> copy(
      Map<AttendanceDate, AttendanceRecord> m) {
    return new TreeMap<>(m);
  }
}
