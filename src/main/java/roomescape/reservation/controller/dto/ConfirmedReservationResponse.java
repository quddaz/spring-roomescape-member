package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.service.dto.ConfirmedReservationResult;

public record ConfirmedReservationResponse(
        Long id,
        boolean confirmed,
        String name,
        LocalDate date,
        Long timeId,
        LocalTime startAt,
        Long themeId,
        String themeName
) implements ReservationResponse {

    public static ConfirmedReservationResponse from(ConfirmedReservationResult result) {
        return new ConfirmedReservationResponse(
                result.id(),
                true,
                result.name(),
                result.date(),
                result.timeId(),
                result.startAt(),
                result.themeId(),
                result.themeName()
        );
    }
}
