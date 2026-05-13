package roomescape.reservation.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import roomescape.reservation.service.dto.ReservationResult;

@Repository
public class JdbcReservationQueryRepository implements ReservationQueryRepository {

    private static final String RESERVATION_RESULT_BASE_SELECT = """
                 SELECT r.id,
                       r.name AS reservation_name,
                       r.date,
                       rt.id AS time_id,
                       rt.start_at,
                       t.id AS theme_id,
                       t.name AS theme_name
                FROM reservation AS r
                INNER JOIN reservation_time AS rt ON r.time_id = rt.id
                INNER JOIN theme AS t ON rt.theme_id = t.id
            """;

    private static final RowMapper<ReservationResult> reservationResultRowMapper = (resultSet, rowNum) ->
            ReservationResult.of(
                    resultSet.getLong("id"),
                    resultSet.getString("reservation_name"),
                    resultSet.getDate("date").toLocalDate(),
                    resultSet.getLong("time_id"),
                    resultSet.getTime("start_at").toLocalTime(),
                    resultSet.getLong("theme_id"),
                    resultSet.getString("theme_name")
            );

    private final JdbcTemplate jdbcTemplate;

    public JdbcReservationQueryRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReservationResult> findAll() {
        String sql = RESERVATION_RESULT_BASE_SELECT;
        return jdbcTemplate.query(sql, reservationResultRowMapper);
    }

    @Override
    public List<ReservationResult> findAllByName(final String name) {
        String sql = RESERVATION_RESULT_BASE_SELECT + "WHERE r.name = ?";
        return jdbcTemplate.query(sql, reservationResultRowMapper, name);
    }

}
