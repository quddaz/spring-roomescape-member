package roomescape.reservation.repository;

import java.util.List;
import roomescape.reservation.service.dto.ReservationResult;

public interface ReservationQueryRepository {

    List<ReservationResult> findAll();

    List<ReservationResult> findAllByName(String name);

}
