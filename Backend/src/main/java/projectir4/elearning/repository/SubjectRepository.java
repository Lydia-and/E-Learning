package projectir4.elearning.repository;

import projectir4.elearning.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findById(long id);
    Subject deleteById(long id);
    Optional<Subject> findByName(String name);
}
