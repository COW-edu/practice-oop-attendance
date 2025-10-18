package attendance.presentation;

import attendance.application.AttendanceService;
import attendance.application.dto.command.FixCommand;
import attendance.application.dto.view.HistoryView;
import attendance.application.dto.view.RegisterView;
import attendance.application.dto.view.RiskViewRow;
import attendance.application.error.AppException;
import attendance.domain.model.AttendanceRecord;
import attendance.domain.value.AttendanceDate;
import attendance.domain.value.AttendanceTime;
import attendance.domain.value.Nickname;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class ConsoleApp {

  private final InputView input;
  private final ResultView result;
  private final AttendanceService service;
  private final ZoneId zone;

  public ConsoleApp(InputView input, ResultView result, AttendanceService service, ZoneId zone) {
    this.input = input;
    this.result = result;
    this.service = service;
    this.zone = zone;
  }

  public void run() {
    while (true) {
      try {
        showMenu();
        var opt = CommandParser.parseMenu(input.readLine());
        if (opt == MenuOption.QUIT) {
          return;
        }
        if (opt == MenuOption.ATTEND) {
          onAttend();
          continue;
        }
        if (opt == MenuOption.FIX) {
          onFix();
          continue;
        }
        if (opt == MenuOption.HISTORY) {
          onHistory();
          continue;
        }
        if (opt == MenuOption.RISK) {
          onRisk();
          continue;
        }
      } catch (AppException e) {
        result.printError(e.getMessage());
      } catch (RuntimeException e) {
        result.printError("처리 중 오류가 발생했습니다." + e.getMessage());
      }
    }
  }

  private void showMenu() {
    var today = LocalDate.now(zone);
    result.printEnter();
    result.printMenuHeader(ViewFormatter.todayLine(today));
    result.printMenu();
  }

  private void onAttend() {
    result.askNickname();
    var nickname = CommandParser.parseNickname(input.readLine());
    result.askRegisterTime();
    LocalTime hhmm = CommandParser.parseRegisterTime(input.readLine());
    ZonedDateTime when = hhmm.atDate(LocalDate.now(zone)).atZone(zone);

    RegisterView res = service.register(nickname, when);
    result.printRegisterResult(res);
    if (res.status() == RegisterView.Status.DUPLICATE) {
      return;
    }

    var rec = service.findRecord(nickname, new AttendanceDate(when.toLocalDate())).orElse(null);
    if (rec != null) {
      result.printRegisterLine(rec);
    }
  }

  private void onFix() {
    result.askNickname();
    var nickname = CommandParser.parseNickname(input.readLine());
    result.askFixDate();
    var date = CommandParser.parseFixDay(input.readLine(), zone);
    result.askFixTime();
    AttendanceTime newTime = CommandParser.parseFixTimeOrAbsent(input.readLine());

    var before = service.findRecord(nickname, date).orElse(null);
    AttendanceRecord after = applyFix(nickname, date, newTime);
    if (before != null && after != null) {
      result.printFixLine(before, after);
    }
  }

  private AttendanceRecord applyFix(Nickname n, AttendanceDate d, AttendanceTime t) {
    var dummy = service.fix(new FixCommand(n, d, t));
    return service.findRecord(n, d).orElse(null);
  }

  private void onHistory() {
    result.askNickname();
    var nickname = CommandParser.parseNickname(input.readLine());
    var until = new AttendanceDate(LocalDate.now(zone).minusDays(1));
    HistoryView v = service.history(nickname, until);
    result.printHistory(v);
  }

  private void onRisk() {
    var until = new AttendanceDate(LocalDate.now(zone).minusDays(1));
    List<RiskViewRow> viewRows = service.riskView(until).rows();
    result.printRisk(viewRows);
  }
}
