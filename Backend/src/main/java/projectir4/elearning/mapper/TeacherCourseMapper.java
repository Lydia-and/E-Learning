package projectir4.elearning.mapper;

import org.mapstruct.Mapper;
import projectir4.elearning.dto.TeacherCourseDTO;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class, UserMapper.class})
public interface TeacherCourseMapper {

    TeacherCourseDTO toDTO(TeacherCourse course);

//    if (teacher.getTeacherCourses() != null) {
//        List<TeacherCourseDTO> courseDTOs = teacher.getTeacherCourses().stream()
//                .map(course -> {
//                    TeacherCourseDTO cDto = new TeacherCourseDTO();
//                    cDto.setId(course.getId());
//                    cDto.setId(course.getSubject().getId());
//                    cDto.setTeacherRole(course.getTeacherRole());
//                    return cDto;
//                }).collect(Collectors.toList());
//        dto.setTeacherCourses(courseDTOs);
//    }

//    if (dto.getTeacherCourses() != null) {
//        for (TeacherCourseDTO cDto : dto.getTeacherCourses()) {
//            TeacherCourse course = new TeacherCourse();
//            course.setTeacherRole(cDto.getTeacherRole());
//            course.setTeacher(teacher);
//
//            teacher.getTeacherCourses().add(course);
//        }
//    }
}
