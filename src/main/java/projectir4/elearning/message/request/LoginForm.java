package projectir4.elearning.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginForm {
    @NotBlank
    @Size(min= 3, max = 50)
    private String username;


    @NotBlank
    @Size(min = 6, max =40)
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
