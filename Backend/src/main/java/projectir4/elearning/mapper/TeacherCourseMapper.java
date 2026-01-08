package projectir4.elearning.mapper;

import org.mapstruct.Mapper;
import projectir4.elearning.dto.TeacherCourseDTO;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class, UserMapper.class})
public interface TeacherCourseMapper {

    TeacherCourseDTO toDTO(TeacherCourse course);
}
