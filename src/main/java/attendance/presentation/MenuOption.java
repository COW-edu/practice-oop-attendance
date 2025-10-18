package attendance.presentation;

import java.util.Map;
import org.assertj.core.util.Preconditions;

public enum MenuOption {
  ATTEND, FIX, HISTORY, RISK, QUIT;

  public static MenuOption fromInput(String raw) {
    if (raw == null) throw new IllegalArgumentException("메뉴에서 1, 2, 3, 4 또는 Q를 입력해 주세요.");
    var key = raw.trim();
    if (key.equals("1")) return ATTEND;
    if (key.equals("2")) return FIX;
    if (key.equals("3")) return HISTORY;
    if (key.equals("4")) return RISK;
    if (key.equalsIgnoreCase("Q")) return QUIT;
    throw new IllegalArgumentException("메뉴에서 1, 2, 3, 4 또는 Q를 입력해 주세요.");
  }
}
