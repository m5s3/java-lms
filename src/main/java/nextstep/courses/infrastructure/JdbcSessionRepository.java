package nextstep.courses.infrastructure;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import nextstep.courses.domain.Course;
import nextstep.courses.domain.Image;
import nextstep.courses.domain.Session;
import nextstep.courses.domain.SessionRepository;
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

    /**
     *
     * public Course findById(Long id) {
     *         String sql = "select id, title, creator_id, created_at, updated_at from course where id = ?";
     *         RowMapper<Course> rowMapper = (rs, rowNum) -> new Course(
     *                 rs.getLong(1),
     *                 rs.getString(2),
     *                 rs.getLong(3),
     *                 toLocalDateTime(rs.getTimestamp(4)),
     *                 toLocalDateTime(rs.getTimestamp(5)));
     *         return jdbcTemplate.queryForObject(sql, rowMapper, id);
     *     }
     *
     *     private LocalDateTime toLocalDateTime(Timestamp timestamp) {
     *         if (timestamp == null) {
     *             return null;
     *         }
     *         return timestamp.toLocalDateTime();
     *     }
     * @param id
     * @return
     */
    @Override
    public Session findById(Long id) {
        String sql = "select s.id, s.title, s.image, s.start_date"
                    + ",s.end_date, s.state, s.type"
                    + ",c.id, c.title, c.creator_id, c.created_at"
                    + "from session s "
                    + "JOIN course c"
                    + "ON s.course_id = c.id"
                    + "WHERE id = ?";
        RowMapper<Session> rowMapper = (rs, rowNum) -> {
            Course course = new Course(rs.getLong("c.id"), rs.getString("c.title"),
                    rs.getLong("c.creator_id"), toLocalDateTime(rs.getTimestamp("c.created_at")),
                    null);
            return  new Session.Builder(rs.getLong("s.id"))
                    .title(rs.getString("s.title"))
                    .image(new Image(rs.getString("s.image")))
                    .sessionDuration(toLocalDateTime(rs.getTimestamp("s.start_date")), toLocalDateTime(rs.getTimestamp("s.end_date")))
                    .course(course)
                    .sessionType();

        };
        return null;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
}
