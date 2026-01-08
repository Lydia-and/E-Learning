package projectir4.elearning.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import projectir4.elearning.dto.EnrollmentDTO;
import projectir4.elearning.model.Enrollment;
import projectir4.elearning.model.Student;
import projectir4.elearning.model.Subject;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class})
public interface EnrollmentMapper {

    @Autowired
    SubjectMapper subjectMapper = null;

    default EnrollmentDTO toDTO(Enrollment enrollment) {
        if (enrollment == null) return null;

        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setGrade(enrollment.getGrade());
        dto.setSubject(subjectMapper.toDTO(enrollment.getSubject()));
        return dto;
    }


    default Enrollment toEntity(EnrollmentDTO dto, Student student, Subject subject) {
        if (dto == null) return null;

        Enrollment enrollment = new Enrollment();
        enrollment.setId(dto.getId());
        enrollment.setGrade(dto.getGrade());
        enrollment.setStudent(student);
        enrollment.setSubject(subject);

        return enrollment;
    }
}
