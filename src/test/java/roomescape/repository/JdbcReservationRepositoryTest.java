package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.JdbcReservationQueryRepository;
import roomescape.reservation.repository.JdbcReservationRepository;
import roomescape.reservation.service.dto.ReservationResult;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.JdbcReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.JdbcThemeRepository;

@RoomescapeRepositoryTest
class JdbcReservationRepositoryTest {

    private JdbcReservationRepository jdbcReservationRepository;
    private JdbcReservationQueryRepository jdbcReservationQueryRepository;
    private JdbcReservationTimeRepository jdbcReservationTimeRepository;
    private JdbcThemeRepository jdbcThemeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcReservationRepository = new JdbcReservationRepository(jdbcTemplate);
        jdbcReservationQueryRepository = new JdbcReservationQueryRepository(jdbcTemplate);
        jdbcReservationTimeRepository = new JdbcReservationTimeRepository(jdbcTemplate);
        jdbcThemeRepository = new JdbcThemeRepository(jdbcTemplate);
    }

    @Test
    @DisplayName("예약 저장")
    void reservation_save_success() {
        //given
        String name = "쿠다";
        LocalDate date = LocalDate.parse("2023-08-05");
        LocalTime time = LocalTime.parse("10:00");
        Theme theme = createTheme("미술관의 밤");

        ReservationTime reservationTime = jdbcReservationTimeRepository.save(ReservationTime.createNew(time, theme));

        Reservation reservation = Reservation.createNew(name, date, reservationTime.getId());
        //when
        Reservation result = jdbcReservationRepository.save(reservation);
        Reservation saved = jdbcReservationRepository.findById(result.getId())
                .orElseThrow();

        // then
        assertThat(result).isEqualTo(saved);
    }

    @Test
    @DisplayName("예약 저장 중복 예외")
    void reservation_save_whenDuplicate_throws() {
        // given
        LocalDate date = LocalDate.parse("2026-08-06");
        LocalTime time = LocalTime.parse("10:00");
        Theme theme = createTheme("미술관의 밤");

        ReservationTime reservationTime = jdbcReservationTimeRepository.save(ReservationTime.createNew(time, theme));

        // when & then
        assertThrows(DataIntegrityViolationException.class, () -> {
            jdbcReservationRepository.save(
                    Reservation.createNew("쿠다", date, reservationTime.getId())
            );
            jdbcReservationRepository.save(
                    Reservation.createNew("아루", date, reservationTime.getId())
            );
        });
    }

    @Test
    @DisplayName("예약 전체 조회")
    void reservation_findAll_success() {
        //given
        LocalDate date = LocalDate.parse("2026-08-06");
        LocalTime time = LocalTime.parse("10:00");
        Theme theme = createTheme("미술관의 밤");

        ReservationTime reservationTime = jdbcReservationTimeRepository.save(ReservationTime.createNew(time, theme));
        jdbcReservationRepository.save(Reservation.createNew("쿠다", date, reservationTime.getId()));

        //when
        List<ReservationResult> reservations = jdbcReservationQueryRepository.findAll();

        //then
        assertThat(reservations).hasSize(1);
    }

    @Test
    @DisplayName("예약 삭제")
    void reservation_delete_success() {
        // given
        LocalDate date = LocalDate.parse("2026-08-06");
        LocalTime time = LocalTime.parse("10:00");
        Theme theme = createTheme("미술관의 밤");

        ReservationTime reservationTime = jdbcReservationTimeRepository.save(ReservationTime.createNew(time, theme));

        Reservation reservation = jdbcReservationRepository.save(
                Reservation.createNew("쿠다", date, reservationTime.getId())
        );

        int beforeSize = jdbcReservationQueryRepository.findAll().size();

        // when
        jdbcReservationRepository.deleteById(reservation.getId());

        // then
        int afterSize = jdbcReservationQueryRepository.findAll().size();

        assertThat(afterSize).isEqualTo(beforeSize - 1);
    }

    @Test
    @DisplayName("예약 업데이트")
    void update_success() {
        // given
        LocalDate date = LocalDate.parse("2026-08-06");
        LocalTime time = LocalTime.parse("10:00");
        Theme theme = createTheme("미술관의 밤");

        ReservationTime reservationTime = jdbcReservationTimeRepository.save(ReservationTime.createNew(time, theme));

        Reservation reservation = jdbcReservationRepository.save(
                Reservation.createNew("쿠다", date, reservationTime.getId())
        );

        // when
        LocalDate newDate = date.plusDays(1);
        LocalTime newTime = LocalTime.parse("11:00");
        ReservationTime newReservationTime = jdbcReservationTimeRepository.save(
                ReservationTime.createNew(newTime, theme));

        jdbcReservationRepository.update(reservation.modify(newDate, newReservationTime.getId()));

        Reservation updated = jdbcReservationRepository.findById(reservation.getId())
                .orElseThrow();

        // then
        assertThat(updated.getDate()).isEqualTo(newDate);
        assertThat(updated.getTimeId()).isEqualTo(newReservationTime.getId());
    }

    private Theme createTheme(final String name) {
        return jdbcThemeRepository.save(
                Theme.createNew(name, "추리 테마", "https://example.com/theme.png")
        );
    }

}
