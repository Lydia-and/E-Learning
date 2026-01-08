package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Student extends User {

    //manage and not ignore so that when calling we get to see the enrollments only
    @JsonIgnoreProperties("student")
    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Enrollment> enrollments;

}
