package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.service.dto.ConfirmedReservationResult;
import roomescape.reservation.service.dto.ReservationResult;
import roomescape.reservation.service.dto.WaitingReservationResult;

public sealed interface ReservationResponse permits ConfirmedReservationResponse, WaitingReservationResponse {

    Long id();

    boolean confirmed();

    String name();

    LocalDate date();

    Long timeId();

    LocalTime startAt();

    Long themeId();

    String themeName();

    static ReservationResponse from(ReservationResult result) {
        if (result instanceof WaitingReservationResult waitingResult) {
            return WaitingReservationResponse.from(waitingResult);
        }
        return ConfirmedReservationResponse.from((ConfirmedReservationResult) result);
    }
}
