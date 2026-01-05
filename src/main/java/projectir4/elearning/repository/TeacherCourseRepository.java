package projectir4.elearning.repository;

import projectir4.elearning.model.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    Optional<TeacherCourse> findById(long id);
    TeacherCourse deleteById(long id);

}
