package attendance.presentation;

import attendance.application.dto.view.HistoryView;
import attendance.application.dto.view.HistoryView.Line;
import attendance.domain.model.AttendanceRecord;
import attendance.domain.model.Status;
import attendance.domain.policy.LateRule;
import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public final class ViewFormatter {

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM월 dd일 E요일 HH:mm",
      Locale.KOREAN);
  private static final Map<Status, String> LABEL = Map.of(Status.ON_TIME, "출석", Status.LATE, "지각",
      Status.ABSENT, "결석");

  public static String todayLine(java.time.LocalDate today) {
    return today.format(DateTimeFormatter.ofPattern("오늘은 MM월 dd일 E요일입니다.", Locale.KOREAN));
  }

  public static String registerLine(AttendanceRecord record, LateRule rule) {
    var st = record.status(rule);
    var dt = formatDateTime(record.date(), record.time());
    return dt + " (" + label(st) + ")";
  }

  public static String fixLine(AttendanceRecord before, AttendanceRecord after, LateRule rule) {
    var left = registerLine(before, rule);
    var right = registerLine(after, rule);
    return left + " -> " + right + " 수정 완료!";
  }

  public static String historyLine(Line l) {
    return l.date().getValue().format(DateTimeFormatter.ofPattern("MM월 dd일 E요일 ", Locale.KOREAN))
        + formatTime(l.time()) + " (" + label(l.status()) + ")";
  }

  public static String historySummary(HistoryView v) {
    return "출석: " + v.onTimeCount() + "회\n지각: " + v.lateCount() + "회\n결석: " + v.absentCount() + "회";
  }

  private static String formatTime(AttendanceTime time) {
    if (time.isAbsent()) {
      return "--:--";
    }
    var t = time.value();
    return (t.getHour() < 10 ? "0" : "") + t.getHour()
        + ":" +
        (t.getMinute() < 10 ? "0" : "") + t.getMinute();
  }

  private static String formatDateTime(AttendanceDate date, AttendanceTime time) {
    if (time.isAbsent()) {
      return date.getValue().format(DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN))
          + " " + formatTime(time);
    }
    var ldt = LocalDateTime.of(date.getValue(), time.value());
    return ldt.format(DATE_FMT);
  }

  private static String label(Status s) {
    var lb = LABEL.get(s);
    if (lb != null) {
      return lb;
    }
    return s.name();
  }

  private ViewFormatter() {
  }
}
