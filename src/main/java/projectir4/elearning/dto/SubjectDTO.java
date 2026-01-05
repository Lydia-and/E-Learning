package projectir4.elearning.dto;

import java.util.List;

public class SubjectDTO {

    private Long id;
    private String name;
    private int coefficient;
    private List<Long> teacherIds;
    private List<EnrollmentDTO> enrollments;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public int getCoefficient() {return coefficient;}

    public void setCoefficient(int coefficient) {this.coefficient = coefficient;}

    public List<Long> getTeacherIds() {return teacherIds;}

    public void setTeacherIds(List<Long> teacherIds) {this.teacherIds = teacherIds;}

    public List<EnrollmentDTO> getEnrollments() {return enrollments;}

    public void setEnrollments(List<EnrollmentDTO> enrollments) {this.enrollments = enrollments;}
}
