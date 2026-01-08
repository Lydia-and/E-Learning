package projectir4.elearning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projectir4.elearning.model.RequestStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EnrollmentRequestDTO {

    private Long id;
    private StudentDTO student;
    private TeacherCourseDTO teacherCourse;
    private RequestStatus status;
}
