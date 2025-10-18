package attendance.presentation;

import attendance.application.dto.view.HistoryView;
import attendance.application.dto.view.RegisterView;
import attendance.application.dto.view.RegisterView.Status;
import attendance.application.dto.view.RiskView;
import attendance.application.dto.view.RiskViewRow;
import attendance.domain.model.AttendanceRecord;
import attendance.domain.model.RiskLevel;
import attendance.domain.policy.LateRule;
import java.io.PrintStream;
import java.util.List;

public class ResultView {

  private final PrintStream out;
  private final LateRule lateRule;

  public ResultView(PrintStream out, LateRule lateRule) {
    this.out = out;
    this.lateRule = lateRule;
  }

  public void printMenuHeader(String todayLine) {
    out.println(todayLine);
  }

  public void printMenu() {
    out.println("기능을 선택해 주세요.");
    out.println("1. 출석 확인");
    out.println("2. 출석 수정");
    out.println("3. 크루별 출석 기록 확인");
    out.println("4. 제적 위험자 확인");
    out.println("Q. 종료");
  }

  public void askNickname() {
    out.println("닉네임을 입력해 주세요.");
  }

  public void askRegisterTime() {
    out.println("등교 시간을 입력해 주세요.");
  }

  public void askFixDate() {
    out.println("수정할 날짜를 입력해 주세요.");
  }

  public void askFixTime() {
    out.println("수정할 시간을 입력해 주세요.");
  }

  public void printRegisterResult(RegisterView r) {
    var s = r.status();
    if (s == Status.DUPLICATE) {
      out.println("이미 오늘 출석이 있습니다. 변경하려면 수정 기능을 사용하세요.");
      return;
    }
    out.println("출석이 등록되었습니다.");
  }

  public void printRegisterLine(AttendanceRecord rec) {
    out.println(ViewFormatter.registerLine(rec, lateRule));
  }

  public void printFixLine(AttendanceRecord before, AttendanceRecord after) {
    out.println(ViewFormatter.fixLine(before, after, lateRule));
  }

  public void printHistory(HistoryView view) {
    view.lines().forEach(l -> out.println(ViewFormatter.historyLine(l)));
    out.println(ViewFormatter.historySummary(view));
  }

  public void printRisk(List<RiskViewRow> viewRows) {
    out.println("제적 위험자 조회 결과");
    viewRows.forEach(r -> {
      var name = r.nickname().getValue();
      var a = r.absentCount();
      var l = r.lateCount();
      var level = toKorean(r.level());
      out.println("- " + name + ": 결석 " + a + "회, 지각 " + l + "회 (" + level + ")");
    });
  }

  private String toKorean(RiskLevel level) {
    if (level == RiskLevel.FIRE) {
      return "제적";
    }
    if (level == RiskLevel.TALK) {
      return "면담";
    }
    if (level == RiskLevel.WARNING) {
      return "경고";
    }
    return "해당 없음";
  }

  public void printEnter() {
    out.println();
  }

  public void printError(String msg) {
    out.println("[ERROR] " + msg);
  }
}
