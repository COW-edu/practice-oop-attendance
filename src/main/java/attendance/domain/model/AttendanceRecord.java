package attendance.domain.model;

import attendance.domain.policy.LateRule;
import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;

public final class AttendanceRecord {

  private final Nickname nickname;
  private final AttendanceDate date;
  private final AttendanceTime time;

  private AttendanceRecord(Nickname n, AttendanceDate d, AttendanceTime t) {
    nickname = n;
    date = d;
    time = t;
  }

  public static AttendanceRecord of(Nickname n, AttendanceDate d, AttendanceTime t) {
    return new AttendanceRecord(n, d, t);
  }

  public AttendanceRecord withTime(AttendanceTime newTime) {
    return new AttendanceRecord(nickname, date, newTime);
  }

  public Status status(LateRule rule) {
   return rule.judge(date.getValue(), time);
  }

  public Nickname nickname() {
    return nickname;
  }

  public AttendanceDate date() {
    return date;
  }

  public AttendanceTime time() {
    return time;
  }
}
