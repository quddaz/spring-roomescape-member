package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.dto.ReservationResult;

public interface ReservationRepository {

    void deleteById(long id);

    void deleteById(long id, String name);

    Reservation save(Reservation reservation);

    int update(Reservation reservation);

    boolean existsByDateAndTimeId(LocalDate date, long timeId);

    boolean existsWaitingByNameAndDateAndTimeId(String name, LocalDate date, long timeId);

    boolean existsByIdAndName(long id, String name);

    boolean existsConfirmedByDateAndTimeIdExcludingId(long id, LocalDate date, long timeId);

    boolean existsByTimeId(long timeId);

    boolean existsByThemeId(long themeId);

    List<ReservationResult> findAll();

    List<ReservationResult> findAllByName(final String name);

    Optional<Reservation> findById(long id);

    Optional<ReservationResult> findWaitingResultById(long id);

}
