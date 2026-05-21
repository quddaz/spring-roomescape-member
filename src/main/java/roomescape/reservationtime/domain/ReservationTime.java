package roomescape.reservationtime.domain;

import java.time.LocalTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import roomescape.global.domain.DomainPreconditions;
import roomescape.reservationtime.exception.ReservationTimeValidationException;

@Getter
@EqualsAndHashCode(of = "id")
public class ReservationTime {

    private final Long id;
    private final LocalTime startAt;
    

    private ReservationTime(final Long id, final LocalTime startAt) {
        validateStartAt(startAt);

        this.id = id;
        this.startAt = startAt;
    }

    public static ReservationTime createNew(final LocalTime startAt) {
        return new ReservationTime(null, startAt);
    }

    public static ReservationTime of(final long id, final LocalTime startAt) {
        return new ReservationTime(id, startAt);
    }

    private void validateStartAt(LocalTime startAt) {
        DomainPreconditions.requireNonNull(startAt, new ReservationTimeValidationException(List.of("예약 시간은 비어있을 수 없습니다.")));
    }

    public ReservationTime withId(final long id) {
        return new ReservationTime(id, this.startAt);
    }

}
