package projectir4.elearning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {

    private Long id;
    private String name;
    private int coefficient;

}
