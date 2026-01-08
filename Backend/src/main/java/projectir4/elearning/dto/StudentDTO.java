package projectir4.elearning.dto;

import lombok.Getter;
import lombok.Setter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {

    private Long id;
    private String username;
    private String email;
}
