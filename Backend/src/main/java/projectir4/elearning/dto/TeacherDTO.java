package projectir4.elearning.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeacherDTO {

    private Long id;
    private String username;
    private String email;
    private String number;
    private List<TeacherCourseDTO> teacherCourses;
}