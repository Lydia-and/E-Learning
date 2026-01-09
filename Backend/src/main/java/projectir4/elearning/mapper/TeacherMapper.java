package projectir4.elearning.mapper;

import projectir4.elearning.dto.TeacherCourseDTO;
import projectir4.elearning.dto.TeacherDTO;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import org.springframework.stereotype.Component;
import projectir4.elearning.model.TeacherCourse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeacherMapper {

    public TeacherDTO toDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setUsername(teacher.getUsername());
        dto.setEmail(teacher.getEmail());

        return dto;
    }

    public Teacher toEntity(TeacherDTO dto) {
        Teacher teacher = new Teacher();
        teacher.setUsername(dto.getUsername());
        teacher.setEmail(dto.getEmail());
        teacher.setTeacherCourses(new ArrayList<>());

        return teacher;
    }
}
