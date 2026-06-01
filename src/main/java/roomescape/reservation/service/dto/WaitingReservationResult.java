package roomescape.reservation.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;

public record WaitingReservationResult(
        Long id,
        boolean confirmed,
        String name,
        LocalDate date,
        Long timeId,
        LocalTime startAt,
        Long themeId,
        String themeName,
        Integer waitingRank
) implements ReservationResult {

    public static WaitingReservationResult from(Reservation reservation, ReservationTime reservationTime,
                                                Integer waitingRank) {
        return new WaitingReservationResult(
                reservation.getId(),
                false,
                reservation.getName(),
                reservation.getDate(),
                reservation.getTimeId(),
                reservationTime.getStartAt(),
                reservationTime.getTheme().getId(),
                reservationTime.getTheme().getName(),
                waitingRank
        );
    }
}
