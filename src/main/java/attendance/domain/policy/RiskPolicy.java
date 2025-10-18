package attendance.domain.policy;

import attendance.domain.model.RiskLevel;

public class RiskPolicy {

  private final int latePerAbsent;
  private final int warnThreshold;
  private final int talkThreshold;
  private final int fireThreshold;

  public RiskPolicy(int latePerAbsent, int warnThreshold, int talkThreshold, int fireThreshold) {
    this.latePerAbsent = latePerAbsent;
    this.warnThreshold = warnThreshold;
    this.talkThreshold = talkThreshold;
    this.fireThreshold = fireThreshold;
  }

  public RiskLevel level(int absentCount, int lateCount) {
    int eq = absentEquivalent(absentCount, lateCount);
    if (eq >= fireThreshold) {
      return RiskLevel.FIRE;
    }
    if (eq >= talkThreshold) {
      return RiskLevel.TALK;
    }
    if (eq >= warnThreshold) {
      return RiskLevel.WARNING;
    }
    return RiskLevel.NONE;
  }

  public int absentEquivalent(int absentCount, int lateCount) {
    return absentCount + lateCount / latePerAbsent;
  }
}
