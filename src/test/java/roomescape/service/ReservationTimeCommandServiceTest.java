package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeConstraintException;
import roomescape.reservationtime.exception.ReservationTimeDuplicateException;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.reservationtime.service.ReservationTimeCommandService;
import roomescape.service.stub.FakeReservationRepository;
import roomescape.service.stub.FakeReservationTimeRepository;
import roomescape.service.stub.FakeThemeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeNotFoundException;
import roomescape.theme.repository.ThemeRepository;

class ReservationTimeCommandServiceTest {

    private ReservationTimeCommandService reservationTimeCommandService;
    private ReservationRepository reservationRepository;
    private ThemeRepository themeRepository;
    private ReservationTimeRepository reservationTimeRepository;

    @BeforeEach
    void setUp() {
        reservationRepository = new FakeReservationRepository();
        reservationTimeRepository = new FakeReservationTimeRepository(reservationRepository);
        themeRepository = new FakeThemeRepository();
        reservationTimeCommandService = new ReservationTimeCommandService(
                reservationTimeRepository,
                reservationRepository,
                themeRepository
        );
    }

    @Test
    @DisplayName("없는 테마 예약 시간 예외")
    void save_whenThemeNotExists_throws() {
        assertThatThrownBy(() -> reservationTimeCommandService.save(LocalTime.of(10, 0), 999L))
                .isInstanceOf(ThemeNotFoundException.class);
    }

    @Test
    @DisplayName("같은 테마 내 같은 시작 시간 중복 생성 예외")
    void save_whenDuplicateStartAtInTheme_throws() {
        Theme theme = themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));
        reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(10, 0), theme));

        assertThatThrownBy(() -> reservationTimeCommandService.save(LocalTime.of(10, 0), theme.getId()))
                .isInstanceOf(ReservationTimeDuplicateException.class);
    }

    @Test
    @DisplayName("예약 시간 저장 성공")
    void save_success() {
        Theme theme = themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));

        reservationTimeCommandService.save(LocalTime.of(10, 0), theme.getId());

        assertThat(reservationTimeRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("예약이 있는 시간 삭제 예외")
    void delete_whenReserved_throws() {
        Theme theme = themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));
        ReservationTime time = reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(10, 0), theme));
        reservationRepository.save(
                Reservation.createNew("쿠다", LocalDate.now().plusDays(1), time.getId())
        );

        assertThatThrownBy(() -> reservationTimeCommandService.deleteById(time.getId()))
                .isInstanceOf(ReservationTimeConstraintException.class);
    }

    @Test
    @DisplayName("예약 시간 삭제 성공")
    void delete_success() {
        Theme theme = themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));
        ReservationTime time = reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(10, 0), theme));

        reservationTimeCommandService.deleteById(time.getId());

        assertThat(reservationTimeRepository.findAll()).isEmpty();
    }
}
