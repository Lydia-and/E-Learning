package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Enrollment {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnoreProperties("enrollments")
    //persist to avoid deleting the student for ex when deleting the enrollment
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn//(name = "student_id")
    private Student student;

    @JsonIgnoreProperties("enrollments")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn//(name = "subject_id")
    private Subject subject;

    private double grade;

}
