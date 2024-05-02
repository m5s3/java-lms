package nextstep.courses.infrastructure;

import nextstep.courses.CannotRegisterException;
import nextstep.courses.domain.Course;
import nextstep.courses.domain.CourseRepository;
import nextstep.courses.domain.Session;
import nextstep.users.domain.NsUser;
import nextstep.users.domain.NsUserTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class CourseRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseRepositoryTest.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository = new JdbcCourseRepository(jdbcTemplate);
    }

    @Test
    void crud() {
        Course course = new Course("TDD, 클린 코드 with Java", 1L);
        courseRepository.save(course);
        Course findCourse = courseRepository.findById(course.getId());
        assertThat(findCourse).isEqualTo(course);
        LOGGER.debug("Course: {}", course);
    }

    @Test
    void findWithSessionsById_테스트() {
        Course course = courseRepository.findWithSessionsById(1L);
        LOGGER.debug("Course: {}", course);
        assertThat(course.countOfSession()).isEqualTo(3);
    }

    @Test
    void 무료수강_등록() throws CannotRegisterException {
        Course course = courseRepository.findWithSessionsById(1L);
        Long sessionId = 1L;
        course.register(sessionId, NsUserTest.JAVAJIGI);
    }
}
