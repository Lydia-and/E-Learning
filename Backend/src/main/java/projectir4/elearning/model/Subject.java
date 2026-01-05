package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Subject {

    @OneToMany(mappedBy = "subject", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonBackReference
    private Set<TeacherCourse> teacherCourses = new HashSet<>();


    @JsonIgnoreProperties("subject")
    //eager so that it will fetch them without needing to ask for them directly
    @OneToMany(mappedBy = "subject", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private int coefficient;


    public Subject() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public Set<Enrollment> getEnrollments() { return enrollments; }

    public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }

    public Set<TeacherCourse> getTeacherCourses() {return teacherCourses;}

    public void setTeacherCourses(Set<TeacherCourse> teacherCourses) {this.teacherCourses = teacherCourses;}
}
