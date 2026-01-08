package projectir4.elearning.repository;

import projectir4.elearning.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectir4.elearning.model.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findById(long id);
    Enrollment deleteById(long id);
    List<Enrollment> findAllByStudentId(Long studentId);
    List<Enrollment> findAllBySubjectIdIn(List<Long> subjectIds);
}
