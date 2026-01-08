package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
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

}
