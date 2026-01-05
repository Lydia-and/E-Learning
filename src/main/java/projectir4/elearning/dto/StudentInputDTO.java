package projectir4.elearning.dto;

import java.util.List;

public class StudentInputDTO {
    private List<EnrollmentInputDTO> enrollments;

    public List<EnrollmentInputDTO> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<EnrollmentInputDTO> enrollments) {
        this.enrollments = enrollments;
    }
}
