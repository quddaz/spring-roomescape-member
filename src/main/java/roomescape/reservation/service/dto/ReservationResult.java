package roomescape.reservation.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;

public record ReservationResult(
        Long id,
        String name,
        LocalDate date,
        Long timeId,
        LocalTime startAt,
        Long themeId,
        String themeName
) {

    public static ReservationResult from(Reservation reservation, Theme theme, java.time.LocalTime startAt) {
        return new ReservationResult(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                reservation.getTimeId(),
                startAt,
                theme.getId(),
                theme.getName()
        );
    }
}
