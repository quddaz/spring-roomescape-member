package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.time.TimeManager;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.exception.ReservationResourceNotFoundException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationSlotRepository;
import roomescape.reservation.service.dto.ReservationResult;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeResourceNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSlotRepository reservationSlotRepository;
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
        ReservationSlot slot = reservationSlotRepository.findOrCreate(date, reservationTime.getId());

        if (alreadyReserved) {
            Reservation savedReservation = reservationRepository.saveWaiting(reservation, slot);
            return findSavedWaiting(name, date, timeId, savedReservation.getId());
        }
        Reservation savedReservation = reservationRepository.saveConfirmed(reservation, slot);
        return ReservationResult.confirmed(savedReservation, reservationTime);
    }

    @Transactional
    public void deleteByAdmin(final long id) {
        Optional<Reservation> confirmedReservation = reservationRepository.findById(id);
        reservationRepository.deleteByAdmin(id);
        confirmedReservation.map(Reservation::getSlotId)
                .ifPresent(this::promoteFirstWaitingReservation);
    }

    @Transactional
    public void deleteByUser(final long id, final String name) {
        if (reservationRepository.existsByIdAndName(id, name)) {
            Optional<Reservation> confirmedReservation = reservationRepository.findById(id);
            reservationRepository.deleteByUser(id, name);
            confirmedReservation.map(Reservation::getSlotId)
                    .ifPresent(this::promoteFirstWaitingReservation);
            return;
        }
        reservationPolicy.validateForDelete(findReservation(id), name);
    }

    @Transactional
    public void update(final long id, final String name, final LocalDate date, final Long timeId) {
        Reservation reservation = findReservation(id);

        ReservationTime reservationTime = findReservationTime(timeId);
        Reservation modifiedReservation = reservation.modify(date, reservationTime.getId());
        ReservationSlot slot = reservationSlotRepository.findOrCreate(date, reservationTime.getId());
        reservationPolicy.validateForUpdate(
                modifiedReservation,
                name,
                reservationRepository.existsConfirmedByDateAndTimeIdExcludingId(id, date, timeId),
                reservationTime,
                timeManager.nowDateTime()
        );

        int updateRowCount = reservationRepository.update(modifiedReservation, slot);
        reservationPolicy.validateSingleRowUpdate(updateRowCount);
        if (!reservation.getSlotId().equals(slot.getId())) {
            promoteFirstWaitingReservation(reservation.getSlotId());
        }
    }

    private Reservation findReservation(final long id) {
        return reservationRepository.findById(id)
                .orElseThrow(ReservationResourceNotFoundException::new);
    }

    private ReservationTime findReservationTime(final Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(ReservationTimeResourceNotFoundException::new);
    }

    private void promoteFirstWaitingReservation(final Long slotId) {
        reservationRepository.findFirstWaitingBySlotId(slotId)
                .ifPresent(waitingReservation -> {
                    reservationRepository.saveConfirmed(waitingReservation, waitingReservation.getSlot());
                    reservationRepository.deleteWaitingById(waitingReservation.getId());
                });
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
