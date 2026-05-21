package roomescape.reservationtime.exception;

import roomescape.global.exception.exception.ResourceInUseException;

public class ReservationTimeInUseException extends ResourceInUseException {
    public ReservationTimeInUseException() {
        super("예약이 존재하는 시간은 삭제할 수 없습니다.");
    }
}
