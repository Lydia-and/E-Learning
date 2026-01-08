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
        teacher.setUsername(dto.getUsername());
        teacher.setEmail(dto.getEmail());
        teacher.setNumber(dto.getNumber());
        teacher.setTeacherCourses(new ArrayList<>());
        if (dto.getTeacherCourses() != null) {
            for (TeacherCourseDTO cDto : dto.getTeacherCourses()) {
                TeacherCourse course = new TeacherCourse();
                course.setTeacherRole(cDto.getRole());
                course.setTeacher(teacher);

                teacher.getTeacherCourses().add(course);
            }
        }
        return teacher;
    }
}
