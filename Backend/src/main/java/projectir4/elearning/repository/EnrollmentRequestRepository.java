package projectir4.elearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectir4.elearning.model.EnrollmentRequest;
import projectir4.elearning.model.Student;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Long> {
    Optional<EnrollmentRequest> findById(Long id);
    List<EnrollmentRequest> findByTeacherCourseTeacherUsername(String username);
    List<EnrollmentRequest> findByStudentUsername(String username);
    boolean existsByStudentAndTeacherCourse(Student student, TeacherCourse course);
}
