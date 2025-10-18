package attendance.application.dto.command;

import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;
import java.util.Objects;

public class FixCommand {

  private final Nickname nickname;
  private final AttendanceDate date;
  private final AttendanceTime time;

  public FixCommand(Nickname nickname, AttendanceDate date, AttendanceTime time) {
    this.nickname = Objects.requireNonNull(nickname, "nickname");
    this.date = Objects.requireNonNull(date, "date");
    this.time = Objects.requireNonNull(time, "time");
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
