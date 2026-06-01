package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import roomescape.global.domain.DomainPreconditions;
import roomescape.reservation.exception.ReservationPastDateException;
import roomescape.reservation.exception.ReservationPermissionDeniedException;
import roomescape.reservation.exception.ReservationValidationException;

@Getter
@EqualsAndHashCode(of = "id")
public class Reservation {

    private static final int NAME_MAX_LENGTH = 10;

    private final Long id;
    private final String name;
    private final LocalDateTime createdAt;
    private final ReservationSlot slot;

    private Reservation(final Long id, final String name, final LocalDateTime createdAt,
                        final ReservationSlot slot) {
        validate(name, slot);
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.slot = slot;
    }

    public static Reservation createNew(final String name, final LocalDate date, final Long timeId) {
        return new Reservation(null, name, null, ReservationSlot.createNew(timeId, date));
    }

    public static Reservation of(final Long id, final String name, final LocalDateTime createdAt,
                                 final ReservationSlot slot) {
        return new Reservation(id, name, createdAt, slot);
    }

    public Reservation withIdAndSlot(final long id, final ReservationSlot slot) {
        return new Reservation(id, name, createdAt, slot);
    }

    public Reservation modify(final LocalDate newDate, final Long newTimeId) {
        return new Reservation(id, name, createdAt, ReservationSlot.createNew(newTimeId, newDate));
    }

    public LocalDate getDate() {
        return slot.getDate();
    }

    public Long getTimeId() {
        return slot.getTimeId();
    }

    public Long getSlotId() {
        return slot.getId();
    }

    private static void validate(final String name, final ReservationSlot slot) {
        List<String> errors = new ArrayList<>();

        validateName(name, errors);
        DomainPreconditions.collectIfNull(slot, errors, "예약 슬롯 정보가 없습니다.");

        if (!errors.isEmpty()) {
            throw new ReservationValidationException(errors);
        }
    }

    private static void validateName(final String name, final List<String> errors) {
        DomainPreconditions.collectIfNull(name, errors, "예약자 이름은 비어있을 수 없습니다.");
        if (name == null) {
            return;
        }

        DomainPreconditions.collectIfFalse(name.length() <= NAME_MAX_LENGTH, errors, "예약자 이름은 최대 10자까지 입력할 수 있습니다.");
        DomainPreconditions.collectIfFalse(!name.isBlank(), errors, "예약자 이름은 비어있을 수 없습니다.");
    }

    public void validateNotPast(LocalTime time, LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(getDate(), time);

        if (reservationDateTime.isBefore(now)) {
            throw new ReservationPastDateException(
                    "예약 날짜는 과거일 수 없습니다."
            );
        }
    }

    public void validateOwner(String requesterName) {
        if (!this.name.equals(requesterName)) {
            throw new ReservationPermissionDeniedException(
                    "예약자만 예약을 수정하거나 취소할 수 있습니다."
            );
        }
    }

}
