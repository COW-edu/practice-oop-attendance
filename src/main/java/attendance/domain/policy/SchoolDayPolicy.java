package attendance.domain.policy;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class SchoolDayPolicy {

  public boolean isSchoolDay(LocalDate date) {
    var w = date.getDayOfWeek();
    if (w == DayOfWeek.SATURDAY) {
      return false;
    }
    if (w == DayOfWeek.SUNDAY) {
      return false;
    }
    return true;
  }
}
