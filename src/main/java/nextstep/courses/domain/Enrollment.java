package nextstep.courses.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import nextstep.courses.CannotRegisterException;
import nextstep.payments.domain.Payment;
import nextstep.users.domain.NsUser;

public class Enrollment {

    private Long id;

    private Students students;
    private int studentCapacity;
    private Money fee;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Enrollment(Students students, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(students, 0, 0, createdAt, updatedAt);
    }

    private Enrollment(Students students, int studentCapacity, long fee, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.students = students;
        this.studentCapacity = studentCapacity;
        this.fee = new Money(fee);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Students getStudents() {
        return students;
    }

    public int getStudentCapacity() {
        return studentCapacity;
    }

    public long getFee() {
        if (Objects.isNull(fee)) {
            return 0;
        }
        return fee.longValue();
    }

    public static Enrollment createFreeEnrollment(Students students, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Enrollment(students, 0, 0, createdAt, updatedAt);
    }

    public static Enrollment createPaidEnrollment(Students students, int studentCapacity, long fee, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Enrollment(students, studentCapacity, fee, createdAt, updatedAt);
    }

    public static Enrollment createEnrollment(Students students, int studentCapacity, long fee, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (studentCapacity > 0 && fee > 0) {
            return createPaidEnrollment(students, studentCapacity, fee, createdAt, updatedAt);
        }
        return createFreeEnrollment(students, createdAt, updatedAt);
    }

    public void enroll(NsUser student) {
        this.students.admit(student);
    }

    public void enroll(NsUser student, Payment payment) throws CannotRegisterException {
        if (isFull()) {
            throw new CannotRegisterException("수강인원이 가득 찾습니다");
        }
        if (!payment.isEqualAmount(fee)) {
            throw new CannotRegisterException("결제 금액이 다릅니다");
        }
        this.students.admit(student);
    }

    public int countOfEnrolledStudent() {
        return this.students.size();
    }

    private boolean isFull() {
        return studentCapacity == students.size();
    }

    public void updateId(long id) {
        this.id = id;
    }
}
