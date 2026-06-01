package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.time.TimeManager;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.exception.ReservationResourceNotFoundException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.ReservationResult;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeResourceNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final TimeManager timeManager;
    private final ReservationPolicy reservationPolicy;

    public List<ReservationResult> getAll() {
        return reservationRepository.findAll();
    }

    public List<ReservationResult> getAllByName(final String name) {
        return reservationRepository.findAllByName(name);
    }

    @Transactional
    public ReservationResult save(final String name, final LocalDate date, final Long timeId) {
        ReservationTime reservationTime = findReservationTime(timeId);

        Reservation reservation = Reservation.createNew(name, date, reservationTime.getId());
        reservationPolicy.validateForSave(
                reservationRepository.existsWaitingByNameAndDateAndTimeId(name, date, timeId),
                reservation,
                reservationTime,
                timeManager.nowDateTime()
        );

        boolean alreadyReserved = reservationRepository.existsByDateAndTimeId(date, timeId);
        Reservation savedReservation = reservationRepository.save(reservation);

        if (alreadyReserved) {
            return findSavedWaiting(name, date, timeId, savedReservation.getId());
        }
        return ReservationResult.confirmed(savedReservation, reservationTime);
    }

    @Transactional
    public void deleteById(final long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void deleteById(final long id, final String name) {
        if (reservationRepository.existsByIdAndName(id, name)) {
            reservationRepository.deleteById(id, name);
            return;
        }
        reservationPolicy.validateForDelete(findReservation(id), name);
    }

    @Transactional
    public void update(final long id, final String name, final LocalDate date, final Long timeId) {
        Reservation reservation = findReservation(id);

        ReservationTime reservationTime = findReservationTime(timeId);
        Reservation modifiedReservation = reservation.modify(date, reservationTime.getId());
        reservationPolicy.validateForUpdate(
                modifiedReservation,
                name,
                reservationRepository.existsConfirmedByDateAndTimeIdExcludingId(id, date, timeId),
                reservationTime,
                timeManager.nowDateTime()
        );

        int updateRowCount = reservationRepository.update(modifiedReservation);
        reservationPolicy.validateSingleRowUpdate(updateRowCount);
    }

    private Reservation findReservation(final long id) {
        return reservationRepository.findById(id)
                .orElseThrow(ReservationResourceNotFoundException::new);
    }

    private ReservationTime findReservationTime(final Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(ReservationTimeResourceNotFoundException::new);
    }

    private ReservationResult findSavedWaiting(final String name, final LocalDate date, final Long timeId,
                                               final Long reservationId) {
        return reservationRepository.findWaitingResultById(reservationId)
                .filter(reservation -> reservation.name().equals(name)
                        && reservation.date().equals(date)
                        && reservation.timeId().equals(timeId))
                .orElseThrow(ReservationResourceNotFoundException::new);
    }

}
