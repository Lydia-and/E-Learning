package projectir4.elearning.dto;

public class TeacherCourseDTO {

    private Long id;
    private Long teacherId;
    private Long subjectId;
    private String role;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getTeacherId() {return teacherId;}

    public void setTeacherId(Long teacherId) {this.teacherId = teacherId;}

    public Long getSubjectId() {return subjectId;}

    public void setSubjectId(Long subjectId) {this.subjectId = subjectId;}

    public String getRole() {return role;}

    public void setRole(String role) {this.role = role;}
}
