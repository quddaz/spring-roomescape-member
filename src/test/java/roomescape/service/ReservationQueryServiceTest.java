package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.reservation.service.dto.ReservationResult;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.service.stub.FakeReservationRepository;
import roomescape.service.stub.FakeReservationTimeRepository;
import roomescape.theme.domain.Theme;

class ReservationQueryServiceTest {

    private ReservationQueryService reservationQueryService;
    private ReservationRepository reservationRepository;
    private ReservationTimeRepository reservationTimeRepository;

    @BeforeEach
    void setUp() {
        reservationRepository = new FakeReservationRepository();
        reservationTimeRepository = new FakeReservationTimeRepository(reservationRepository);
        reservationQueryService = new ReservationQueryService(reservationRepository);
    }

    @Test
    @DisplayName("예약 전체 조회")
    void getAll_success() {
        ReservationTime time = saveReservationTime();
        reservationRepository.save(Reservation.createNew("쿠다", LocalDate.now().plusDays(1), time));

        assertThat(reservationQueryService.getAll())
                .extracting(ReservationResult::name)
                .containsExactly("쿠다");
    }

    @Test
    @DisplayName("이름으로 예약 조회")
    void getAllByName_success() {
        ReservationTime time = saveReservationTime();
        reservationRepository.save(Reservation.createNew("쿠다", LocalDate.now().plusDays(1), time));
        reservationRepository.save(Reservation.createNew("피케이", LocalDate.now().plusDays(2), time));

        assertThat(reservationQueryService.getAllByName("쿠다"))
                .extracting(ReservationResult::name)
                .containsExactly("쿠다");
    }

    private ReservationTime saveReservationTime() {
        Theme theme = Theme.of(1L, "미술관의 밤", "설명", "thumb");
        return reservationTimeRepository.save(ReservationTime.createNew(LocalTime.of(10, 0), theme));
    }
}
