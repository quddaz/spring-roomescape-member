package roomescape.reservation.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.ReservationSlot;

@Repository
public class JdbcReservationSlotRepository implements ReservationSlotRepository {

    private static final RowMapper<ReservationSlot> reservationSlotRowMapper = (resultSet, rowNum) -> ReservationSlot.of(
            resultSet.getLong("id"),
            resultSet.getLong("time_id"),
            resultSet.getDate("date").toLocalDate()
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcReservationSlotRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReservationSlot findOrCreate(final LocalDate date, final Long timeId) {
        return findByDateAndTimeId(date, timeId)
                .orElseGet(() -> save(ReservationSlot.createNew(timeId, date)));
    }

    private Optional<ReservationSlot> findByDateAndTimeId(final LocalDate date, final Long timeId) {
        String sql = "SELECT id, date, time_id FROM reservation_slot WHERE date = ? AND time_id = ?";
        return jdbcTemplate.query(sql, reservationSlotRowMapper, Date.valueOf(date), timeId)
                .stream()
                .findFirst();
    }

    private ReservationSlot save(final ReservationSlot slot) {
        String sql = "INSERT INTO reservation_slot (date, time_id) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setDate(1, Date.valueOf(slot.getDate()));
            preparedStatement.setLong(2, slot.getTimeId());
            return preparedStatement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("[ERROR] 예약 슬롯 ID를 생성하지 못했습니다.");
        }
        return slot.withId(key.longValue());
    }
}
