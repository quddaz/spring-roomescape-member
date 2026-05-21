package roomescape.reservation.exception;

import roomescape.global.exception.exception.ResourceNotFoundException;

public class ReservationResourceNotFoundException extends ResourceNotFoundException {
    public ReservationResourceNotFoundException() {
        super("찾는 예약이 없습니다.");
    }
}
