package nextstep.courses.infrastructure;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import nextstep.courses.domain.Course;
import nextstep.courses.domain.Image;
import nextstep.courses.domain.Session;
import nextstep.courses.domain.SessionRepository;
import nextstep.courses.domain.SessionState;
import nextstep.courses.domain.SessionType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("sessionRepository")
public class JdbcSessionRepository implements SessionRepository {

    private JdbcOperations jdbcTemplate;

    public JdbcSessionRepository(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Session session) {
        String sql = "insert into session(title, image, start_date, end_date, course_id, state, type) "
                    + "values(?, ?, ?, ?, ?, ?, ?)";
        Map<String, LocalDateTime> sessionDuration = session.getSessionDuration();
        return jdbcTemplate.update(sql,
                session.getId(),
                session.getImage(),
                sessionDuration.get("startDate"),
                sessionDuration.get("endDate"),
                session.getCourse(),
                session.getState(),
                session.getSessionType());
    }

    @Override
    public Session findById(Long id) {
        String sql = "select s.id as session_id, s.title, s.image, s.start_date "
                    + ",s.end_date, s.state, s.type "
                    + ",c.id as course_id, c.title as course_title, c.creator_id, c.created_at as course_created_at "
                    + "from session as s "
                    + "JOIN course as c "
                    + "ON s.course_id = c.id "
                    + "WHERE s.id = ?";
        RowMapper<Session> rowMapper = (rs, rowNum) -> {
            Course course = new Course(rs.getLong("course_id"), rs.getString("course_title"),
                    rs.getLong("creator_id"), toLocalDateTime(rs.getTimestamp("course_created_at")),
                    null);

            SessionType sessionType = SessionType.findType(rs.getString("type"));
            SessionState state = SessionState.findState(rs.getString("state"));

            return new Session.Builder(rs.getLong("session_id"))
                    .title(rs.getString("title"))
                    .image(new Image(rs.getString("image")))
                    .sessionDuration(toLocalDateTime(rs.getTimestamp("start_date")),
                            toLocalDateTime(rs.getTimestamp("end_date")))
                    .course(course)
                    .sessionType(sessionType)
                    .state(state)
                    .build();
        };
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
}
