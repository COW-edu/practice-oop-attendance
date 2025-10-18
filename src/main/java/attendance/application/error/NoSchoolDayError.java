package attendance.application.error;

public class NoSchoolDayError extends AppException {
  public NoSchoolDayError(String dateText) {
     super(dateText + "은 등교일이 아닙니다.");
  }
}
