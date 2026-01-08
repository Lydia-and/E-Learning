package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @JoinColumn//(name = "subject_id")
    private Subject subject;

    private String teacherRole;

}
