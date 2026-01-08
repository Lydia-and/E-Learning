package projectir4.elearning.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCourseDTO {

    private Long id;
    private SubjectDTO subject;
    private TeacherDTO teacher;
    private String teacherRole;

}
