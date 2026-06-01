package roomescape.reservation.repository;

import java.time.LocalDate;
import roomescape.reservation.domain.ReservationSlot;

public interface ReservationSlotRepository {

    ReservationSlot findOrCreate(LocalDate date, Long timeId);
}
