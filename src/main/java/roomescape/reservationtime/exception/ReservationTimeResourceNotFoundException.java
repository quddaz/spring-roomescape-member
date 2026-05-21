package roomescape.reservationtime.exception;

import roomescape.global.exception.exception.ResourceNotFoundException;

public class ReservationTimeResourceNotFoundException extends ResourceNotFoundException {
    public ReservationTimeResourceNotFoundException() {
        super("찾는 예약 시간이 없습니다.");
    }
}
