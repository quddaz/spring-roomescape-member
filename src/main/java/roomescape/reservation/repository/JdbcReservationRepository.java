package roomescape.reservation.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.service.dto.ReservationResult;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@Repository
public class JdbcReservationRepository implements ReservationRepository {

    private static final String CONFIRMED_SELECT = """
            SELECT rc.id,
                   rc.name AS reservation_name,
                   rc.created_at,
                   rs.id AS slot_id,
                   rs.date,
                   rt.id AS time_id,
                   rt.start_at,
                   t.id AS theme_id,
                   t.name AS theme_name,
                   t.description,
                   t.thumbnail_url
            FROM reservation_confirmed AS rc
            INNER JOIN reservation_slot AS rs ON rc.reservation_slot_id = rs.id
            INNER JOIN reservation_time AS rt ON rs.time_id = rt.id
            INNER JOIN theme AS t ON rt.theme_id = t.id
            """;

    private static final String WAITING_SELECT = """
            SELECT rw.id,
                   rw.name AS reservation_name,
                   rw.created_at,
                   rs.id AS slot_id,
                   rs.date,
                   rt.id AS time_id,
                   rt.start_at,
                   t.id AS theme_id,
                   t.name AS theme_name,
                   t.description,
                   t.thumbnail_url,
                   (
                       SELECT COUNT(*) + 1
                       FROM reservation_waiting AS earlier
                       WHERE earlier.reservation_slot_id = rw.reservation_slot_id
                         AND (
                             earlier.created_at < rw.created_at
                             OR (earlier.created_at = rw.created_at AND earlier.id < rw.id)
                         )
                   ) AS waiting_rank
            FROM reservation_waiting AS rw
            INNER JOIN reservation_slot AS rs ON rw.reservation_slot_id = rs.id
            INNER JOIN reservation_time AS rt ON rs.time_id = rt.id
            INNER JOIN theme AS t ON rt.theme_id = t.id
            """;

    private static final RowMapper<Reservation> confirmedReservationRowMapper = (resultSet, rowNum) -> Reservation.of(
            resultSet.getLong("id"),
            resultSet.getString("reservation_name"),
            toLocalDateTime(resultSet.getTimestamp("created_at")),
            ReservationSlot.of(
                    resultSet.getLong("slot_id"),
                    resultSet.getLong("time_id"),
                    resultSet.getDate("date").toLocalDate()
            )
    );

    private static final RowMapper<ReservationResult> confirmedResultRowMapper = (resultSet, rowNum) -> {
        Reservation reservation = Reservation.of(
                resultSet.getLong("id"),
                resultSet.getString("reservation_name"),
                toLocalDateTime(resultSet.getTimestamp("created_at")),
                ReservationSlot.of(
                        resultSet.getLong("slot_id"),
                        resultSet.getLong("time_id"),
                        resultSet.getDate("date").toLocalDate()
                )
        );
        ReservationTime reservationTime = ReservationTime.of(
                resultSet.getLong("time_id"),
                resultSet.getTime("start_at").toLocalTime(),
                Theme.of(
                        resultSet.getLong("theme_id"),
                        resultSet.getString("theme_name"),
                        resultSet.getString("description"),
                        resultSet.getString("thumbnail_url")
                )
        );
        return ReservationResult.confirmed(reservation, reservationTime);
    };

    private static final RowMapper<ReservationResult> waitingResultRowMapper = (resultSet, rowNum) -> {
        Reservation reservation = Reservation.of(
                resultSet.getLong("id"),
                resultSet.getString("reservation_name"),
                toLocalDateTime(resultSet.getTimestamp("created_at")),
                ReservationSlot.of(
                        resultSet.getLong("slot_id"),
                        resultSet.getLong("time_id"),
                        resultSet.getDate("date").toLocalDate()
                )
        );
        ReservationTime reservationTime = ReservationTime.of(
                resultSet.getLong("time_id"),
                resultSet.getTime("start_at").toLocalTime(),
                Theme.of(
                        resultSet.getLong("theme_id"),
                        resultSet.getString("theme_name"),
                        resultSet.getString("description"),
                        resultSet.getString("thumbnail_url")
                )
        );

        return ReservationResult.waiting(
                reservation,
                reservationTime,
                resultSet.getObject("waiting_rank", Integer.class)
        );
    };

    private final JdbcTemplate jdbcTemplate;
    private final ReservationSlotRepository reservationSlotRepository;

    public JdbcReservationRepository(final JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, new JdbcReservationSlotRepository(jdbcTemplate));
    }

    @Autowired
    public JdbcReservationRepository(final JdbcTemplate jdbcTemplate,
                                     final ReservationSlotRepository reservationSlotRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.reservationSlotRepository = reservationSlotRepository;
    }

    @Override
    public void deleteById(final long id) {
        if (jdbcTemplate.update("DELETE FROM reservation_confirmed WHERE id = ?", id) == 0) {
            jdbcTemplate.update("DELETE FROM reservation_waiting WHERE id = ?", id);
        }
    }

    @Override
    public void deleteById(final long id, final String name) {
        int confirmedDeleteCount = jdbcTemplate.update(
                "DELETE FROM reservation_confirmed WHERE id = ? AND name = ?",
                id,
                name
        );
        if (confirmedDeleteCount == 0) {
            jdbcTemplate.update("DELETE FROM reservation_waiting WHERE id = ? AND name = ?", id, name);
        }
    }

    @Override
    public Reservation save(final Reservation reservation) {
        ReservationSlot slot = reservationSlotRepository.findOrCreate(reservation.getDate(), reservation.getTimeId());

        if (existsConfirmedBySlotId(slot.getId())) {
            return saveWaiting(reservation, slot);
        }
        return saveConfirmed(reservation, slot);
    }

    @Override
    public int update(final Reservation reservation) {
        ReservationSlot slot = reservationSlotRepository.findOrCreate(reservation.getDate(), reservation.getTimeId());
        final String sql = "UPDATE reservation_confirmed SET reservation_slot_id = ? WHERE id = ?";

        return jdbcTemplate.update(sql, slot.getId(), reservation.getId());
    }

    @Override
    public boolean existsByDateAndTimeId(final LocalDate date, final long timeId) {
        final String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM reservation_confirmed AS rc
                    INNER JOIN reservation_slot AS rs ON rc.reservation_slot_id = rs.id
                    WHERE rs.date = ? AND rs.time_id = ?
                )
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, Date.valueOf(date), timeId));
    }

    @Override
    public boolean existsWaitingByNameAndDateAndTimeId(final String name, final LocalDate date, final long timeId) {
        final String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM reservation_slot AS rs
                    INNER JOIN reservation_waiting AS rw
                            ON rw.reservation_slot_id = rs.id
                    WHERE rs.date = ?
                      AND rs.time_id = ?
                      AND rw.name = ?
                )
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                Date.valueOf(date),
                timeId,
                name
        ));
    }

    @Override
    public boolean existsByIdAndName(final long id, final String name) {
        final String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM reservation_confirmed AS rc
                    WHERE rc.id = ? AND rc.name = ?
                    UNION ALL
                    SELECT 1
                    FROM reservation_waiting AS rw
                    WHERE rw.id = ? AND rw.name = ?
                )
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id, name, id, name));
    }

    @Override
    public boolean existsConfirmedByDateAndTimeIdExcludingId(final long id, final LocalDate date, final long timeId) {
        final String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM reservation_confirmed AS rc
                    INNER JOIN reservation_slot AS rs ON rc.reservation_slot_id = rs.id
                    WHERE rc.id <> ?
                      AND rs.date = ?
                      AND rs.time_id = ?
                )
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id, Date.valueOf(date), timeId));
    }

    @Override
    public boolean existsByTimeId(final long timeId) {
        final String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM reservation_slot AS rs
                    LEFT JOIN reservation_confirmed AS rc ON rc.reservation_slot_id = rs.id
                    LEFT JOIN reservation_waiting AS rw ON rw.reservation_slot_id = rs.id
                    WHERE rs.time_id = ? AND (rc.id IS NOT NULL OR rw.id IS NOT NULL)
                )
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, timeId));
    }

    @Override
    public boolean existsByThemeId(final long themeId) {
        final String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM reservation_slot AS rs
                    INNER JOIN reservation_time AS rt ON rs.time_id = rt.id
                    LEFT JOIN reservation_confirmed AS rc ON rc.reservation_slot_id = rs.id
                    LEFT JOIN reservation_waiting AS rw ON rw.reservation_slot_id = rs.id
                    WHERE rt.theme_id = ? AND (rc.id IS NOT NULL OR rw.id IS NOT NULL)
                )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, themeId));
    }

    @Override
    public List<ReservationResult> findAll() {
        return findAllConfirmedResults();
    }

    @Override
    public List<ReservationResult> findAllByName(final String name) {
        List<ReservationResult> results = new ArrayList<>();
        results.addAll(findConfirmedResultsByName(name));
        results.addAll(findWaitingResultsByName(name));
        results.sort(Comparator.comparing(ReservationResult::date).thenComparing(ReservationResult::startAt));
        return results;
    }

    @Override
    public Optional<Reservation> findById(final long id) {
        String sql = CONFIRMED_SELECT + " WHERE rc.id = ?";

        return jdbcTemplate.query(sql, confirmedReservationRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<ReservationResult> findWaitingResultById(final long id) {
        String sql = WAITING_SELECT + " WHERE rw.id = ?";

        return jdbcTemplate.query(sql, waitingResultRowMapper, id)
                .stream()
                .findFirst();
    }

    private List<ReservationResult> findAllConfirmedResults() {
        String sql = CONFIRMED_SELECT + " ORDER BY date, start_at";
        return jdbcTemplate.query(sql, confirmedResultRowMapper);
    }

    private List<ReservationResult> findConfirmedResultsByName(final String name) {
        String sql = CONFIRMED_SELECT + " WHERE rc.name = ?";
        return jdbcTemplate.query(sql, confirmedResultRowMapper, name);
    }

    private List<ReservationResult> findWaitingResultsByName(final String name) {
        String sql = WAITING_SELECT + " WHERE rw.name = ?";
        return jdbcTemplate.query(sql, waitingResultRowMapper, name);
    }

    private Reservation saveConfirmed(final Reservation reservation, final ReservationSlot slot) {
        String sql = "INSERT INTO reservation_confirmed (name, reservation_slot_id) VALUES (?, ?)";
        long id = insertReservation(sql, reservation.getName(), slot.getId());
        return Reservation.of(id, reservation.getName(), reservation.getCreatedAt(), slot);
    }

    private Reservation saveWaiting(final Reservation reservation, final ReservationSlot slot) {
        String sql = "INSERT INTO reservation_waiting (name, reservation_slot_id) VALUES (?, ?)";
        long id = insertReservation(sql, reservation.getName(), slot.getId());
        return Reservation.of(id, reservation.getName(), reservation.getCreatedAt(), slot);
    }

    private long insertReservation(final String sql, final String name, final Long slotId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, slotId);
            return preparedStatement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("[ERROR] 예약 ID를 생성하지 못했습니다.");
        }
        return key.longValue();
    }

    private boolean existsConfirmedBySlotId(final Long slotId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM reservation_confirmed WHERE reservation_slot_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, slotId));
    }

    private static LocalDateTime toLocalDateTime(final Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
}
