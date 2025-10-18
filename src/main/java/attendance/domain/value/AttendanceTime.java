package attendance.domain.value;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public final class AttendanceTime {

  private static final String ABSENT_TOKEN = "--:--";
  private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

  private final LocalTime time;
  private final boolean absent;

  private AttendanceTime(LocalTime time, boolean absent) {
    this.time = time;
    this.absent = absent;
  }

  public static AttendanceTime of(LocalTime time) {
    Objects.requireNonNull(time, "시간은 빈 값이 될 수 없습니다.");
    return new AttendanceTime(time, false);
  }

  public static AttendanceTime absent() {
    return new AttendanceTime(null, true);
  }

  public static AttendanceTime parseHm(String text) {
    if (text == null) {
      throw new IllegalArgumentException("시간을 입력해 주세요.");
    }
    var t = text.trim();
    if (ABSENT_TOKEN.equals(t)) {
      return AttendanceTime.absent(); // 수정 시 --:-- 입력을 결석으로 처리
    }
    try {
      return AttendanceTime.of(LocalTime.parse(t, HM));
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("시간 형식은 HH:mm 또는 --:-- 입니다.");
    }
  }

  public LocalTime value() {
    if (absent) {
      throw new IllegalStateException("결석엔 시간 값이 없습니다.");
    }
    return time;
  }

  public boolean isAbsent() {
    return absent;
  }
}



