package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.service.dto.WaitingReservationResult;

public record WaitingReservationResponse(
        Long id,
        boolean confirmed,
        String name,
        LocalDate date,
        Long timeId,
        LocalTime startAt,
        Long themeId,
        String themeName,
        Integer waitingRank
) implements ReservationResponse {

    public static WaitingReservationResponse from(WaitingReservationResult result) {
        return new WaitingReservationResponse(
                result.id(),
                false,
                result.name(),
                result.date(),
                result.timeId(),
                result.startAt(),
                result.themeId(),
                result.themeName(),
                result.waitingRank()
        );
    }
}
