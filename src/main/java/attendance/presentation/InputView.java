package attendance.presentation;

import java.io.InputStream;
import java.util.Scanner;

public class InputView {

  private final Scanner scanner;

  public InputView(InputStream in) {
    this.scanner = new Scanner(in);
  }

  public String readLine() {
    if (!scanner.hasNextLine()) {
      return "Q";
    }
    return scanner.nextLine();
  }
}
