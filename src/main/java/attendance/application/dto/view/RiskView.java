package attendance.application.dto.view;

import attendance.domain.model.RiskLevel;
import java.util.Comparator;
import java.util.List;

public class RiskView {

  private final List<RiskViewRow> rows;

  private RiskView(List<RiskViewRow> rows) {
    this.rows = List.copyOf(rows);
  }

  public static RiskView of(List<RiskViewRow> row) {
    return new RiskView(row.stream().toList());
  }

  public List<RiskViewRow> rows() {
    return rows;
  }

  public boolean isEmpty() {
    return rows.isEmpty();
  }

  private static Comparator<RiskViewRow> comparator() {
    return Comparator
        .comparingInt((RiskViewRow r) -> levelRank(r.level())).reversed()
        .thenComparingInt(RiskView::score).reversed()
        .thenComparing(r -> r.nickname().getValue());
  }

  private static int score(RiskViewRow r) {
    return r.absentCount() * 3 + r.lateCount();
  }

  private static int levelRank(RiskLevel level) {
    if (level == RiskLevel.FIRE) {
      return 3;
    }
    if (level == RiskLevel.TALK) {
      return 2;
    }
    if (level == RiskLevel.WARNING) {
      return 1;
    }
    return 0;
  }
}
