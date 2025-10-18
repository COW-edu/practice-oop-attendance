package attendance.application;

import attendance.domain.model.CrewAttendance;
import attendance.domain.value.Nickname;
import java.util.Optional;

public interface AttendanceRepository {

  Optional<CrewAttendance> findByNickname(Nickname nickname);

  CrewAttendance save(CrewAttendance sheet);
}
