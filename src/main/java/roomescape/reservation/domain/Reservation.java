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
    private final LocalDate date;
    private final Long timeId;
    private final Long themeId;

    private Reservation(final Long id, final String name, final LocalDate date, final Long timeId, final Long themeId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.timeId = timeId;
        this.themeId = themeId;
    }

    public static Reservation createNew(final String name, final LocalDate date, final Long timeId, final Long themeId) {
        validate(name, date, timeId, themeId);
        return new Reservation(null, name, date, timeId, themeId);
    }

    public static Reservation of(final long id, final String name, final LocalDate date, final Long timeId, final Long themeId) {
        validate(name, date, timeId, themeId);
        return new Reservation(id, name, date, timeId, themeId);
    }

    private static void validate(final String name, final LocalDate date, final Long timeId, final Long themeId) {
        List<String> errors = new ArrayList<>();

        validateName(name, errors);
        validateDate(date, errors);
        validateTimeId(timeId, errors);
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

    private static void validateTimeId(final Long timeId, final List<String> errors) {
        DomainPreconditions.collectIfNull(timeId, errors, "예약 시간 정보가 없습니다.");
    }

    private static void validateThemeId(final Long themeId, final List<String> errors) {
        DomainPreconditions.collectIfNull(themeId, errors, "예약 테마 정보가 없습니다.");
    }

    public Reservation withId(final long id) {
        return new Reservation(id, this.name, this.date, this.timeId, this.themeId);
    }

    public Reservation modify(final LocalDate newDate, final Long newTimeId, final Long newThemeId) {
        validate(name, newDate, newTimeId, newThemeId);
        return new Reservation(id, name, newDate, newTimeId, newThemeId);
    }

    public void validateNotPast(LocalTime time, LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time);

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
