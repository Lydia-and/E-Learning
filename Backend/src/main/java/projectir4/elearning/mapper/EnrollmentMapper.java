package projectir4.elearning.mapper;

import projectir4.elearning.dto.EnrollmentDTO;
import projectir4.elearning.model.Enrollment;
import projectir4.elearning.model.Student;
import projectir4.elearning.model.Subject;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {
    public Enrollment toEntity(EnrollmentDTO dto, Student student, Subject subject) {
        Enrollment enrollment = new Enrollment();
        enrollment.setGrade(dto.getGrade());
        enrollment.setStudent(student);
        enrollment.setSubject(subject);
        return enrollment;
    }

    public EnrollmentDTO toDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setGrade(enrollment.getGrade());
        if (enrollment.getStudent() != null) {
            dto.setStudentId(enrollment.getStudent().getId());
        }

        if (enrollment.getSubject() != null) {
            dto.setSubjectId(enrollment.getSubject().getId());
        }
        return dto;
    }
}
