package nextstep.courses.infrastructure;

import java.util.Objects;
import nextstep.courses.domain.Course;
import nextstep.courses.domain.CourseRepository;
import nextstep.courses.domain.Session;
import nextstep.courses.domain.Session.Builder;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository("courseRepository")
public class JdbcCourseRepository implements CourseRepository {
    private JdbcOperations jdbcTemplate;

    public JdbcCourseRepository(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Course course) {
        String sql = "insert into course (title, creator_id, created_at) values(?, ?, ?)";
        return jdbcTemplate.update(sql, course.getTitle(), course.getCreatorId(), course.getCreatedAt());
    }

    @Override
    public Course findById(Long id) {
        String sql = "select id, title, creator_id, created_at, updated_at from course where id = ?";
        RowMapper<Course> rowMapper = (rs, rowNum) -> new Course(
                rs.getLong(1),
                rs.getString(2),
                rs.getLong(3),
                toLocalDateTime(rs.getTimestamp(4)),
                toLocalDateTime(rs.getTimestamp(5)));
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    @Override
    public Course findWithSessionsById(Long id) {
        String sql = "select c.id as course_id, c.title as course_title, c.creator_id as course_creator_id "
                    + ", s.id as session_id, s.title as session_title "
                    + ", e.id as enrollment_id, e.studuent_capacity, e.fee "
                    + ", es.enorllment_id, es.student_id "
                    + "from course c "
                    + "left join session s "
                    + "on c.id = s.course_id "
                    + "left join enrollment e "
                    + "on s.id = e.session_id "
                    + "left join enrollment_student es "
                    + "on es.enrollment_id = e.id "
                    + "where c.id = ?";

        RowMapper<Course> rowMapper = (rs, rowNum) -> {
            Course course = null;
            while (rs.next()) {
                if (Objects.isNull(course)) {
                    course = new Course(rs.getLong("course_id"), rs.getString("course_title"),
                            rs.getLong("course_creator_id"));
                }
                Session session = new Builder(rs.getLong("session_id"))
                    .title(rs.getString("session_title"))
                    .build();
                course.add(session);
            }
            return course;
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
