package nextstep.courses.domain;

import nextstep.payments.domain.Payment;

public interface SessionRepository {

    Session save(Session session);

    Session findById(Long id);

    void updateTitle(Long id, String title);

    void delete(Long id);

    void register(Long id, Long studentId);
    void register(Long id, Long studentId, Payment payment);
}
