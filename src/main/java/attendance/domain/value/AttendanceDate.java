package attendance.domain.value;

import java.time.LocalDate;
import java.util.Objects;

public final class AttendanceDate implements Comparable<AttendanceDate> {

  private final LocalDate value;

  public AttendanceDate(LocalDate value) {
    this.value = Objects.requireNonNull(value, "날짜는 빈 값이 될 수 없습니다.");
  }

  public static AttendanceDate paresIso(String yyyyMMdd) {
    return new AttendanceDate(LocalDate.parse(yyyyMMdd));
  }

  public LocalDate getValue() {
    return value;
  }

  @Override
  public int compareTo(AttendanceDate o) {
    return this.value.compareTo(o.value);
  }
}