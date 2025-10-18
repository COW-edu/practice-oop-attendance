package attendance.application.dto.view;

import attendance.domain.value.AttendanceDate;
import attendance.domain.value.Nickname;
import java.util.Objects;

public class RegisterView {

  public enum Status {REGISTERED, DUPLICATE}

  private final Nickname nickname;
  private final AttendanceDate date;
  private final Status status;

  private RegisterView(Nickname nickname, AttendanceDate date, Status status) {
    this.nickname = Objects.requireNonNull(nickname, "nickname");
    this.date = Objects.requireNonNull(date, "date");
    this.status = Objects.requireNonNull(status, "status");
  }

  public static RegisterView registered(Nickname n, AttendanceDate d) {
    return new RegisterView(n, d, Status.REGISTERED);
  }

  public static RegisterView duplicate(Nickname n, AttendanceDate d) {
    return new RegisterView(n, d, Status.DUPLICATE);
  }

  public Nickname nickname() {
    return nickname;
  }

  public AttendanceDate date() {
    return date;
  }

  public Status status() {
    return status;
  }
}
