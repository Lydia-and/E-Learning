package projectir4.elearning.repository;

import projectir4.elearning.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findById(Long id);
    Teacher deleteById(long id);
    Optional<Teacher> findByEmail(String email);
}
