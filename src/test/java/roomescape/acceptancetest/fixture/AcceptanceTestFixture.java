package roomescape.acceptancetest.fixture;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public final class AcceptanceTestFixture {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public LocalDate today() {
        return LocalDate.now();
    }

    public LocalDate reservationDate() {
        return today().plusDays(1);
    }

    public void createTheme() {
        createTheme(
                "미술관의 밤",
                "추리 테마",
                "https://example.com/theme.png"
        );
    }

    public void createTheme(
            final String name,
            final String description,
            final String thumbnailUrl
    ) {
        jdbcTemplate.update(
                "INSERT INTO theme (name, description, thumbnail_url) VALUES (?, ?, ?)",
                name,
                description,
                thumbnailUrl
        );
    }

    public void createReservationTime(
            final String startAt,
            final Long themeId
    ) {
        jdbcTemplate.update(
                "INSERT INTO reservation_time (start_at, theme_id) VALUES (?, ?)",
                startAt,
                themeId
        );
    }

    public void createReservation(
            final String name,
            final LocalDate date,
            final Long timeId
    ) {
        jdbcTemplate.update(
                "INSERT INTO reservation_slot (date, time_id) VALUES (?, ?)",
                date,
                timeId
        );
        Long slotId = jdbcTemplate.queryForObject(
                "SELECT id FROM reservation_slot WHERE date = ? AND time_id = ?",
                Long.class,
                date,
                timeId
        );
        jdbcTemplate.update(
                "INSERT INTO reservation_confirmed (name, reservation_slot_id) VALUES (?, ?)",
                name,
                slotId
        );
    }

}
