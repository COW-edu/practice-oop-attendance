package attendance.application.error;

public class UnRegisterNameError extends AppException {

  public UnRegisterNameError() {
    super("등록되지 않은 닉네임입니다.");
  }
}
