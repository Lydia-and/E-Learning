package projectir4.elearning.mapper;

import projectir4.elearning.dto.TeacherCourseDTO;
import projectir4.elearning.dto.TeacherDTO;
import projectir4.elearning.model.Teacher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeacherMapper {

    public TeacherDTO toDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setFirstname(teacher.getTeacherFirstName());
        dto.setLastname(teacher.getTeacherLastName());
        dto.setEmail(teacher.getEmail());
        dto.setNumber(teacher.getNumber());

        if (teacher.getTeacherCourses() != null) {
            List<TeacherCourseDTO> courseDTOs = teacher.getTeacherCourses().stream()
                    .map(course -> {
                        TeacherCourseDTO cDto = new TeacherCourseDTO();
                        cDto.setId(course.getId());
                        cDto.setSubjectId(course.getSubject().getId());
                        cDto.setRole(course.getTeacherRole());
                        return cDto;
                    }).collect(Collectors.toList());
            dto.setTeacherCourses(courseDTOs);
        }

        return dto;
    }

    public Teacher toEntity(TeacherDTO dto) {
        Teacher teacher = new Teacher();
        teacher.setTeacherFirstName(dto.getFirstname());
        teacher.setEmail(dto.getEmail());
        teacher.setTeacherLastName(dto.getLastname());
        teacher.setNumber(dto.getNumber());

        return teacher;
    }
}
