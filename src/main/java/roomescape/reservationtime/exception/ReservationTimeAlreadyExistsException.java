package roomescape.reservationtime.exception;

import roomescape.global.exception.exception.AlreadyExistsException;

public class ReservationTimeAlreadyExistsException extends AlreadyExistsException {

    public ReservationTimeAlreadyExistsException() {
        super("같은 테마 내 같은 시작 시간은 중복될 수 없습니다.");
    }
}
