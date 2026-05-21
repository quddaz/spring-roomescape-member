package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import roomescape.global.domain.DomainPreconditions;
import roomescape.reservation.exception.ReservationPastDateException;
import roomescape.reservation.exception.ReservationPermissionDeniedException;
import roomescape.reservation.exception.ReservationValidationException;
import roomescape.reservationtime.domain.ReservationTime;

@Getter
@EqualsAndHashCode(of = "id")
public class Reservation {

    private static final int NAME_MAX_LENGTH = 10;

    private final Long id;
    private final String name;
    private final LocalDate date;
    private final ReservationTime reservationTime;
    private final Long themeId;

    private Reservation(final Long id, final String name, final LocalDate date, final ReservationTime reservationTime, final Long themeId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.reservationTime = reservationTime;
        this.themeId = themeId;
    }

    public static Reservation createNew(final String name, final LocalDate date, final ReservationTime reservationTime, final Long themeId) {
        validate(name, date, reservationTime, themeId);
        return new Reservation(null, name, date, reservationTime, themeId);
    }

    public static Reservation of(final long id, final String name, final LocalDate date, final ReservationTime reservationTime, final Long themeId) {
        validate(name, date, reservationTime, themeId);
        return new Reservation(id, name, date, reservationTime, themeId);
    }

    private static void validate(final String name, final LocalDate date, final ReservationTime reservationTime, final Long themeId) {
        List<String> errors = new ArrayList<>();

        validateName(name, errors);
        validateDate(date, errors);
        validateReservationTime(reservationTime, errors);
        validateThemeId(themeId, errors);

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

    private static void validateDate(final LocalDate date, final List<String> errors) {
        DomainPreconditions.collectIfNull(date, errors, "예약 날짜는 비어있을 수 없습니다.");
    }

    private static void validateReservationTime(final ReservationTime reservationTime, final List<String> errors) {
        DomainPreconditions.collectIfNull(reservationTime, errors, "예약 시간 정보가 없습니다.");
    }

    private static void validateThemeId(final Long themeId, final List<String> errors) {
        DomainPreconditions.collectIfNull(themeId, errors, "예약 테마 정보가 없습니다.");
    }

    public Reservation withId(final long id) {
        return new Reservation(id, this.name, this.date, this.reservationTime, this.themeId);
    }

    public Reservation modify(final LocalDate newDate, final ReservationTime newReservationTime, final Long newThemeId) {
        validate(name, newDate, newReservationTime, newThemeId);
        return new Reservation(id, name, newDate, newReservationTime, newThemeId);
    }

    public void validateNotPast(LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, reservationTime.getStartAt());

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

    public Long getTimeId() {
        return reservationTime.getId();
    }

}
