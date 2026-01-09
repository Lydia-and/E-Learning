package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
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
    @JoinColumn
    private Subject subject;

    @OneToMany(mappedBy = "teacherCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("teacherCourse")
    private Set<EnrollmentRequest> enrollmentRequests = new HashSet<>();

    private String teacherRole;
}
