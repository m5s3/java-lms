package nextstep.courses.domain;

public interface SessionRepository {

    Session save(Session session);

    Session findById(Long id);

    void updateTitle(Long id, String title);

    void delete(Long id);
}
