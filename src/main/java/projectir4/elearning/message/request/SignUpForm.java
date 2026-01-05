package projectir4.elearning.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class SignUpForm {

    @NotBlank
    @Size(min= 3, max = 50)
    private String username;

    @NotBlank
    @Size(min= 3, max = 50)
    private String email;

    private String secretCode;
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max =40)
    private String password;

    public SignUpForm(){}

    public SignUpForm(String username, String email, String secretCode, Set<String> role, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.secretCode = secretCode;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getSecretCode() {return secretCode;}

    public String getEmail() {return email;}
}
