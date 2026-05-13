package roomescape.reservation.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;

public record ReservationResult(
        Long id,
        String name,
        LocalDate date,
        Long timeId,
        LocalTime startAt,
        Long themeId,
        String themeName
) {

    public static ReservationResult of(final Reservation reservation, final ReservationTime reservationTime) {
        return new ReservationResult(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                reservation.getTimeId(),
                reservationTime.getStartAt(),
                reservationTime.getTheme().getId(),
                reservationTime.getTheme().getName()
        );
    }

    public static ReservationResult of(
            final Long id,
            final String name,
            final LocalDate date,
            final Long timeId,
            final LocalTime startAt,
            final Long themeId,
            final String themeName
    ) {
        return new ReservationResult(id, name, date, timeId, startAt, themeId, themeName);
    }
}
