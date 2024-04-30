package nextstep.courses.infrastructure;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.courses.domain.Course;
import nextstep.courses.domain.Image;
import nextstep.courses.domain.Session;
import nextstep.courses.domain.SessionRepository;
import nextstep.courses.domain.SessionState;
import nextstep.courses.domain.SessionType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository("sessionRepository")
public class JdbcSessionRepository implements SessionRepository {

    private JdbcOperations jdbcTemplate;

    public JdbcSessionRepository(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Session save(Session session) {
        String sql = "insert into session(title, image, start_date, end_date, course_id, state, type) "
                    + "values(?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        Map<String, LocalDateTime> sessionDuration = session.getSessionDuration();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql,  new String[]{"id"});

            ps.setString(1, session.getTitle());
            ps.setString(2, session.getImage());
            ps.setTimestamp(3, Timestamp.valueOf(sessionDuration.get("startDate")));
            ps.setTimestamp(4, Timestamp.valueOf(sessionDuration.get("endDate")));
            ps.setLong(5, session.getCourse());
            ps.setString(6, session.getState());
            ps.setString(7, session.getSessionType());

            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        session.updateId(key);
        return session;
    }

    @Override
    public Session findById(Long id) {
        try {
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
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("해당 강의는 존재 하지 않습니다.");
        }
    }

    @Override
    public void updateTitle(Long id, String title) {
        String sql = "update session set title = ? WHERE id = ?";
        jdbcTemplate.update(sql, title, id);
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from session where id = ?";
        jdbcTemplate.update(sql, id);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
}
