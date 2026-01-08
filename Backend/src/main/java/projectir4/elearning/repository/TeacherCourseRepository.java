package projectir4.elearning.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    Optional<TeacherCourse> findById(long id);

    TeacherCourse deleteById(long id);

    List<TeacherCourse> findAllByTeacherId(Long teacherId);

    boolean existsByTeacherIdAndSubjectId(Long teacherId, Long subjectId);

    Optional<TeacherCourse> findTeacherByID(long id);
}
