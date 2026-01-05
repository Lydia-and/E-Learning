package projectir4.elearning.dto;

import java.util.List;

public class TeacherDTO {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String number;
    private List<TeacherCourseDTO> teacherCourses;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getFirstname() {return firstname;}

    public void setFirstname(String firstname) {this.firstname = firstname;}

    public String getLastname() {return lastname;}

    public void setLastname(String lastname) {this.lastname = lastname;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getNumber() {return number;}

    public void setNumber(String number) {this.number = number;}

    public List<TeacherCourseDTO> getTeacherCourses() {return teacherCourses;}

    public void setTeacherCourses(List<TeacherCourseDTO> teacherCourses) {this.teacherCourses = teacherCourses;}
}
