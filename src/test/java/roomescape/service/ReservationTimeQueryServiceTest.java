package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.reservationtime.service.ReservationTimeQueryService;
import roomescape.reservationtime.service.dto.ReservationTimeResult;
import roomescape.service.stub.FakeReservationRepository;
import roomescape.service.stub.FakeReservationTimeRepository;
import roomescape.service.stub.FakeThemeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

class ReservationTimeQueryServiceTest {

    private ReservationTimeQueryService reservationTimeQueryService;
    private ReservationRepository reservationRepository;
    private ThemeRepository themeRepository;
    private ReservationTimeRepository reservationTimeRepository;

    @BeforeEach
    void setUp() {
        reservationRepository = new FakeReservationRepository();
        reservationTimeRepository = new FakeReservationTimeRepository(reservationRepository);
        themeRepository = new FakeThemeRepository();
        reservationTimeQueryService = new ReservationTimeQueryService(reservationTimeRepository);
    }

    @Test
    @DisplayName("예약 시간 전체 조회")
    void findAll_success() {
        Theme theme = themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));
        reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(10, 0), theme));

        assertThat(reservationTimeQueryService.findAll())
                .extracting(ReservationTimeResult::startAt)
                .containsExactly(LocalTime.of(10, 0));
    }

    @Test
    @DisplayName("테마별 예약 시간 조회")
    void findAllByThemeId_success() {
        Theme theme1 = themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));
        Theme theme2 = themeRepository.save(Theme.createNew("우주", "설명", "thumb"));
        reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(10, 0), theme1));
        reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(11, 0), theme2));

        assertThat(reservationTimeQueryService.findAllByThemeId(theme1.getId()))
                .extracting(ReservationTimeResult::startAt)
                .containsExactly(LocalTime.of(10, 0));
    }

    @Test
    @DisplayName("예약 가능 시간 조회 시 이미 예약된 시간 제외")
    void findAvailableTimes_excludesReservedTimes() {
        Theme theme = themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));
        ReservationTime time1 = reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(10, 0), theme));
        ReservationTime time2 = reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(11, 0), theme));
        LocalDate date = LocalDate.now().plusDays(1);
        reservationRepository.save(Reservation.createNew("쿠다", date, time1.getId()));

        List<ReservationTimeResult> availableTimes = reservationTimeQueryService.findAvailableTimes(date, theme.getId());

        assertThat(availableTimes).hasSize(1);
        assertThat(availableTimes.getFirst().id()).isEqualTo(time2.getId());
    }
}
