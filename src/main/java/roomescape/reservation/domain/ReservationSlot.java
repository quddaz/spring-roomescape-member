package roomescape.reservation.domain;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import roomescape.global.domain.DomainPreconditions;
import roomescape.reservation.exception.ReservationValidationException;

@Getter
@EqualsAndHashCode(of = "id")
public class ReservationSlot {

    private final Long id;
    private final Long timeId;
    private final LocalDate date;


    public ReservationSlot(Long id, Long timeId, LocalDate date) {
        validate(date, timeId);
        this.id = id;
        this.timeId = timeId;
        this.date = date;
    }

    public static ReservationSlot createNew(Long timeId, LocalDate date) {
        return new ReservationSlot(null, timeId, date);
    }

    public static ReservationSlot of(Long id, Long timeId, LocalDate date) {
        return new ReservationSlot(id, timeId, date);
    }

    public ReservationSlot withId(Long id) {
        return new ReservationSlot(id, this.timeId, this.date);
    }

    private static void validate(final LocalDate date, final Long timeId) {
        List<String> errors = new ArrayList<>();

        validateDate(date, errors);
        validateTimeId(timeId, errors);

        if (!errors.isEmpty()) {
            throw new ReservationValidationException(errors);
        }
    }

    private static void validateDate(final LocalDate date, final List<String> errors) {
        DomainPreconditions.collectIfNull(date, errors, "예약 날짜는 비어있을 수 없습니다.");
    }

    private static void validateTimeId(final Long timeId, final List<String> errors) {
        DomainPreconditions.collectIfNull(timeId, errors, "예약 시간 정보가 없습니다.");
    }


}
