package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Teacher {

    @JsonIgnoreProperties("teacher")
    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherCourse> teacherCourses;

    @Id
    @GeneratedValue
    private long id;
    private String teacherFirstName;
    private String teacherLastName;
    private String number;
    private String email;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTeacherFirstName() {
        return teacherFirstName;
    }

    public void setTeacherFirstName(String teacherFirstName) {
        this.teacherFirstName = teacherFirstName;
    }

    public String getTeacherLastName() {
        return teacherLastName;
    }

    public void setTeacherLastName(String teacherLastName) {
        this.teacherLastName = teacherLastName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) { this.email = email; }

    public List<TeacherCourse> getTeacherCourses() {return teacherCourses;}

    public void setTeacherCourses(List<TeacherCourse> teacherCourses) {this.teacherCourses = teacherCourses;}
}
