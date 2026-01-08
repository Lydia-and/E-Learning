package projectir4.elearning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Teacher extends User {

    @JsonIgnoreProperties("teacher")
    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherCourse> teacherCourses;

}
