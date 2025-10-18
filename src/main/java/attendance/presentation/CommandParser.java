package attendance.presentation;

import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class CommandParser {

  private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");


  public static MenuOption parseMenu(String line) {
    return MenuOption.fromInput(line);
  }

  public static Nickname parseNickname(String line) {
    if (line == null) {
      throw new IllegalArgumentException("닉네임을 입력해 주세요.");
    }
    var t = line.trim();
    if (!t.isEmpty()) {
      return new Nickname(t);
    }
    throw new IllegalArgumentException("닉네임을 입력해 주세요.");
  }

  public static LocalTime parseRegisterTime(String hhmm) {
    try {
      return LocalTime.parse(require(hhmm, "시간을 입력해 주세요.").trim(), HM);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("시간 형식이 올바르지 않습니다.");
    }
  }

  public static AttendanceTime parseFixTimeOrAbsent(String s) {
    return AttendanceTime.parseHm(s);
  }

  public static AttendanceDate parseFixDay(String day, ZoneId zone) {
    if (day == null) {
      throw new IllegalArgumentException("날짜를 입력해 주세요.");
    }
    var t = day.trim();
    if (!t.matches("\\d{1,2}")) {
      throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다.");
    }

    int d = Integer.parseInt(t);
    var today = LocalDate.now(zone);
    int last = today.lengthOfMonth();

    if (d < 1 || d > last) {
      throw new IllegalArgumentException("이번 달의 유효한 날짜가 아닙니다.");
    }

    var date = LocalDate.of(today.getYear(), today.getMonth(), d);
    return new AttendanceDate(date);
  }

  private static String require(String s, String msg) {
    if (s == null) {
      throw new IllegalArgumentException(msg);
    }
    if (s.trim().isEmpty()) {
      throw new IllegalArgumentException(msg);
    }
    return s;
  }
}