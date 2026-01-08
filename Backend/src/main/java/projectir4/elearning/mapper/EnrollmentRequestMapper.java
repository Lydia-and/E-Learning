package projectir4.elearning.mapper;

import org.mapstruct.Mapper;
import projectir4.elearning.dto.EnrollmentRequestDTO;
import projectir4.elearning.model.EnrollmentRequest;

@Mapper(componentModel = "spring", uses = {UserMapper.class,TeacherCourseMapper.class})
public interface EnrollmentRequestMapper {
    EnrollmentRequestDTO toDTO(EnrollmentRequest request);
}
