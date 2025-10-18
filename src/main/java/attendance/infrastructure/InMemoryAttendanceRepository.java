package attendance.infrastructure;

import attendance.application.AttendanceRepository;
import attendance.domain.model.CrewAttendance;
import attendance.domain.value.Nickname;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAttendanceRepository implements AttendanceRepository {

  private final Map<String, CrewAttendance> store = new ConcurrentHashMap<>();

  @Override
  public Optional<CrewAttendance> findByNickname(Nickname nickname) {
    return Optional.ofNullable(store.get(nickname.getValue()));
  }

  @Override
  public CrewAttendance save(CrewAttendance sheet) {
    store.put(sheet.nickname().getValue(), sheet);
    return sheet;
  }
}
