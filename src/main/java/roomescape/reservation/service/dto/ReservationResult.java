package roomescape.reservation.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;

public sealed interface ReservationResult permits ConfirmedReservationResult, WaitingReservationResult {

    Long id();

    boolean confirmed();

    String name();

    LocalDate date();

    Long timeId();

    LocalTime startAt();

    Long themeId();

    String themeName();

    static ReservationResult confirmed(Reservation reservation, ReservationTime reservationTime) {
        return ConfirmedReservationResult.from(reservation, reservationTime);
    }

    static ReservationResult waiting(Reservation reservation, ReservationTime reservationTime, Integer waitingRank) {
        return WaitingReservationResult.from(reservation, reservationTime, waitingRank);
    }
}
