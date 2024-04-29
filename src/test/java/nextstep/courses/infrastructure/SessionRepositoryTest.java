package nextstep.courses.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import nextstep.courses.domain.Course;
import nextstep.courses.domain.CourseRepository;
import nextstep.courses.domain.Enrollment;
import nextstep.courses.domain.Image;
import nextstep.courses.domain.Session;
import nextstep.courses.domain.SessionRepository;
import nextstep.courses.domain.SessionState;
import nextstep.courses.domain.SessionType;
import nextstep.courses.domain.Students;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

@JdbcTest
class SessionRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SessionRepository sessionRepository;
    private CourseRepository courseRepository;

    Image image;

    @BeforeEach
    void setUp() {
        image = new Image("test.png", 300, 200, 1_000);
        sessionRepository = new JdbcSessionRepository(jdbcTemplate);
        courseRepository = new JdbcCourseRepository(jdbcTemplate);
    }

    @Test
    void crud() {
        Course course = courseRepository.findById(1L);
        LocalDateTime now = LocalDateTime.now();
        Session session = new Session.Builder(1L)
                .title("lms")
                .sessionType(SessionType.FREE)
                .image(image)
                .state(SessionState.RECRUITING)
                .sessionDuration(now.plusDays(5), now.plusDays(10))
                .enrollment(Enrollment.createFreeEnrollment(new Students(new ArrayList<>())))
                .course(course)
                .build();

        int count = sessionRepository.save(session);
        int expected = 1;

        assertThat(count).isEqualTo(expected);
        Session savedSession = sessionRepository.findById(1L);
        LOGGER.debug("Course: {}", savedSession);
    }
}
