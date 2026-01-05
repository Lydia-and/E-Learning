package projectir4.elearning.mapper;

import projectir4.elearning.dto.EnrollmentDTO;
import projectir4.elearning.dto.StudentDTO;
import projectir4.elearning.model.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public StudentDTO toDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstname(student.getFirstname());
        dto.setLastname(student.getLastname());
        dto.setEmail(student.getEmail());
        dto.setTelephone(student.getTelephone());

        if (student.getEnrollments() != null) {
            List<EnrollmentDTO> enrollments = student.getEnrollments().stream()
                    .map(enrollment -> {
                        EnrollmentDTO eDto = new EnrollmentDTO();
                        eDto.setId(enrollment.getId());
                        eDto.setGrade(enrollment.getGrade());
                        eDto.setSubjectId(enrollment.getSubject().getId());
                        return eDto;
                    }).collect(Collectors.toList());
            dto.setEnrollments(enrollments);
        }

        return dto;
    }

    public Student toEntity(StudentDTO dto) {
        Student student = new Student();
        student.setFirstname(dto.getFirstname());
        student.setLastname(dto.getLastname());
        student.setEmail(dto.getEmail());
        student.setTelephone(dto.getTelephone());
        return student;
    }
}
