package roomescape.reservation.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;

public record ConfirmedReservationResult(
        Long id,
        boolean confirmed,
        String name,
        LocalDate date,
        Long timeId,
        LocalTime startAt,
        Long themeId,
        String themeName
) implements ReservationResult {

    public static ConfirmedReservationResult from(Reservation reservation, ReservationTime reservationTime) {
        return new ConfirmedReservationResult(
                reservation.getId(),
                true,
                reservation.getName(),
                reservation.getDate(),
                reservation.getTimeId(),
                reservationTime.getStartAt(),
                reservationTime.getTheme().getId(),
                reservationTime.getTheme().getName()
        );
    }
}
