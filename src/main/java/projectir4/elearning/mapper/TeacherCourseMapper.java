package projectir4.elearning.mapper;

import projectir4.elearning.dto.TeacherCourseDTO;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;
import org.springframework.stereotype.Component;

@Component
public class TeacherCourseMapper {

    public TeacherCourseDTO toDTO(TeacherCourse teacherCourse) {
        TeacherCourseDTO dto = new TeacherCourseDTO();
        dto.setId(teacherCourse.getId());

        if (teacherCourse.getTeacher() != null) {
            dto.setTeacherId(teacherCourse.getTeacher().getId());
        }
        if (teacherCourse.getSubject() != null) {
            dto.setSubjectId(teacherCourse.getSubject().getId());
        }

        dto.setRole(teacherCourse.getTeacherRole());
        return dto;
    }

    public TeacherCourse toEntity(TeacherCourseDTO dto, Teacher teacher, Subject subject) {
        TeacherCourse course = new TeacherCourse();
        course.setTeacher(teacher);
        course.setSubject(subject);
        course.setTeacherRole(dto.getRole());

        return course;
    }
}
