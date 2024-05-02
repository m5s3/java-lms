package nextstep.courses.infrastructure;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.plaf.SeparatorUI;
import nextstep.courses.domain.Course;
import nextstep.courses.domain.CourseRepository;
import nextstep.courses.domain.Enrollment;
import nextstep.courses.domain.Session;
import nextstep.courses.domain.Session.Builder;
import nextstep.courses.domain.SessionState;
import nextstep.courses.domain.Students;
import nextstep.users.domain.NsUser;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
    public Course save(Course course) {
        String sql = "insert into course (title, creator_id, created_at) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});

            ps.setString(1, course.getTitle());
            ps.setLong(2, course.getCreatorId());
            ps.setTimestamp(3, Timestamp.valueOf(course.getCreatedAt()));

            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        course.updateId(key);

        return course;
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
        String sql = "select c.id as course_id, c.title as course_title, c.creator_id as course_creator_id"
                    + ",s.id as session_id, s.title as session_title, s.state as session_state, s.type as session_type"
                    + " from course c"
                    + " left join session s"
                    + " on c.id = s.course_id"
                    + " where c.id = ?";

        RowMapper<Course> rowMapper = (rs, rowNum) -> {
            Course course = new Course(rs.getLong("course_id"), rs.getString("course_title"),
                    rs.getLong("course_creator_id"));;

//            Students students = new Students(new ArrayList<>());
//            Long studentId = rs.getLong("student_id");
//
//            if (Objects.nonNull(studentId)) {
//                students.admit(new NsUser(studentId));
//            }
//
//            Enrollment enrollment = Enrollment.createEnrollment(rs.getString(""));
//
            Session session = new Builder(rs.getLong("session_id"))
                    .title(rs.getString("session_title"))
                    .stateByString(rs.getString("session_state"))
                    .typeByString(rs.getString("session_type"))
                    .enrollment(enrollment)
                    .build();
            course.add(session);
            while (rs.next()) {
                session = new Builder(rs.getLong("session_id"))
                    .title(rs.getString("session_title"))
                    .stateByString(rs.getString("session_state"))
                    .typeByString(rs.getString("session_type"))
                    .build();

                Long studentId = rs.getLong("student_id");
                if (Objects.nonNull(studentId)) {
                    students.admit(new NsUser(studentId));
                }

                course.add(session);
            }
            return course;
        };

        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    private Enrollment findEnrollment(Long sessionId) {

    }

    private Students findStudent(Long enrollmentId) {

    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
}
