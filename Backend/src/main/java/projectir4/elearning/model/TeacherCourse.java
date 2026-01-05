package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class TeacherCourse {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnoreProperties("teacherCourses")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn
    private Teacher teacher;

    @JsonIgnoreProperties("teacherCourses")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn//(name = "subject_id")
    private Subject subject;

    private String teacherRole;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Teacher getTeacher() {return teacher;}

    public void setTeacher(Teacher teacher) {this.teacher = teacher;}

    public Subject getSubject() {return subject;}

    public void setSubject(Subject subject) {this.subject = subject;}

    public String getTeacherRole() {return teacherRole;}

    public void setTeacherRole(String teacherRole) {this.teacherRole = teacherRole;}
}
