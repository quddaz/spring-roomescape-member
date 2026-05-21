package roomescape.reservationtime.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import roomescape.reservationtime.domain.ReservationTime;

public interface ReservationTimeRepository {

    List<ReservationTime> findAll();

    Optional<ReservationTime> findById(long timeId);

    List<ReservationTime> findAvailableTimes(LocalDate date, Long themeId);

    void deleteById(long timeId);

    ReservationTime save(ReservationTime reservationTime);

    boolean existsByStartAt(LocalTime startAt);

}
