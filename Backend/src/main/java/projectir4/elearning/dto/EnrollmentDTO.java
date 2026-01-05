package projectir4.elearning.dto;

public class EnrollmentDTO {

    private Long id;
    //private StudentDTO student;
    private Long studentId;
    private Long subjectId;
    private Double grade;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getSubjectId() {return subjectId;}

    public void setSubjectId(Long subjectId) {this.subjectId = subjectId;}

    public Long getStudentId() {return studentId;}

    public void setStudentId(Long studentId) {this.studentId = studentId;}

    public Double getGrade() {return grade;}

    public void setGrade(Double grade) {this.grade = grade;}

    //public StudentDTO getStudent() {return student;}

    //public void setStudent(StudentDTO student) {this.student = student;}
}
