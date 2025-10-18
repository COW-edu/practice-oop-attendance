package attendance.application.error;

public class AppException extends RuntimeException {

  public AppException(String m) {
    super(m);
  }

  public AppException(String m, Throwable c) {
    super(m, c);
  }
}
