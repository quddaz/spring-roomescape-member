package roomescape.service.stub;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;

public class FakeReservationRepository implements ReservationRepository {

    private final List<Reservation> storage = new ArrayList<>();
    private final Map<Long, Long> themeIdsByTimeId = new HashMap<>();
    private long sequence = 1L;

    public List<Reservation> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public Optional<Reservation> findById(final long id) {
        return storage.stream()
                .filter(reservation -> reservation.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteById(final long id) {
        storage.removeIf(reservation -> reservation.getId().equals(id));
    }

    @Override
    public Reservation save(final Reservation reservation) {
        Reservation saved = reservation;
        if (reservation.getId() == null) {
            saved = reservation.withId(sequence++);
        }
        storage.add(saved);
        return saved;
    }

    @Override
    public boolean existsByDateAndTimeId(final LocalDate date, final long timeId) {
        return storage.stream()
                .anyMatch(reservation ->
                        reservation.getDate().equals(date)
                                && reservation.getTimeId() == timeId
                );
    }

    @Override
    public boolean existsByTimeId(final long timeId) {
        return storage.stream()
                .anyMatch(reservation -> reservation.getTimeId() == timeId);
    }

    @Override
    public boolean existsByThemeId(long themeId) {
        return storage.stream()
                .map(Reservation::getTimeId)
                .map(themeIdsByTimeId::get)
                .anyMatch(savedThemeId -> savedThemeId != null && savedThemeId == themeId);
    }

    @Override
    public void update(Reservation reservation) {
        storage.stream()
                .filter(r -> r.getId().equals(reservation.getId()))
                .findFirst()
                .ifPresent(r -> {
                    storage.remove(r);
                    storage.add(reservation);
                });
    }

    public void recordReservationTime(final long timeId, final long themeId) {
        themeIdsByTimeId.put(timeId, themeId);
    }

}
