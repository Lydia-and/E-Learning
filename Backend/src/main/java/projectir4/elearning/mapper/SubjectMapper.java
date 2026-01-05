package projectir4.elearning.mapper;

import projectir4.elearning.dto.SubjectDTO;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SubjectMapper {

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    public SubjectDTO toDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setCoefficient(subject.getCoefficient());

        // map enrollments
        if (subject.getEnrollments() != null) {
            dto.setEnrollments(subject.getEnrollments().stream()
                    .map(enrollmentMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        // map teacher IDs only
        if (subject.getTeacherCourses() != null) {
            dto.setTeacherIds(subject.getTeacherCourses().stream()
                    .map(TeacherCourse::getTeacher)
                    .filter(Objects::nonNull)
                    .map(Teacher::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Subject toEntity(SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setName(dto.getName());
        subject.setCoefficient(dto.getCoefficient());
        // Relations (teachers, enrollments) à gérer au service ou avec des appels findById
        return subject;
    }
}
