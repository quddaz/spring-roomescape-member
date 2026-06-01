package roomescape.reservation.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.exception.ReservationAlreadyExistsException;
import roomescape.reservation.exception.ReservationUnexpectedUpdateCountException;
import roomescape.reservationtime.domain.ReservationTime;

@Component
public class ReservationPolicy {

    public void validateForSave(final boolean alreadyApplied, final Reservation reservation,
                                final ReservationTime reservationTime, final LocalDateTime now) {
        if (alreadyApplied) {
            throw new ReservationAlreadyExistsException();
        }
        validateNotPast(reservation, reservationTime, now);
    }

    public void validateForUpdate(final Reservation reservation, final String requesterName,
                                  final boolean duplicatedSlot, final ReservationTime reservationTime,
                                  final LocalDateTime now) {
        reservation.validateOwner(requesterName);
        if (duplicatedSlot) {
            throw new ReservationAlreadyExistsException();
        }
        validateNotPast(reservation, reservationTime, now);
    }

    public void validateForDelete(final Reservation reservation, final String requesterName) {
        reservation.validateOwner(requesterName);
    }

    public void validateSingleRowUpdate(final int updateRowCount) {
        if (updateRowCount != 1) {
            throw new ReservationUnexpectedUpdateCountException(updateRowCount);
        }
    }

    private void validateNotPast(final Reservation reservation, final ReservationTime reservationTime,
                                 final LocalDateTime now) {
        reservation.validateNotPast(reservationTime.getStartAt(), now);
    }
}
