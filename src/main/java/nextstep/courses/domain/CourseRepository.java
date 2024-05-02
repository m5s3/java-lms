package nextstep.courses.domain;

public interface CourseRepository {
    Course save(Course course);

    Course findById(Long id);

    Course findWithSessionsById(Long id);
}
