package roomescape.reservation.exception;

import roomescape.global.exception.exception.AlreadyExistsException;

public class ReservationAlreadyExistsException extends AlreadyExistsException {
    public ReservationAlreadyExistsException() {
        super("이미 같은 날짜와 시간에 예약이 존재합니다.");
    }
}
