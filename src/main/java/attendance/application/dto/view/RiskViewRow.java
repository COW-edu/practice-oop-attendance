package attendance.application.dto.view;

import attendance.domain.model.RiskLevel;
import attendance.domain.value.Nickname;

public record RiskViewRow(Nickname nickname, int absentCount, int lateCount, RiskLevel level) {

}
