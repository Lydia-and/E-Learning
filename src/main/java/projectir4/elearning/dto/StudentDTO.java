package projectir4.elearning.dto;

import java.util.List;

public class StudentDTO {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String telephone;
    private List<EnrollmentDTO> enrollments;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getFirstname() {return firstname;}

    public void setFirstname(String firstname) {this.firstname = firstname;}

    public String getLastname() {return lastname;}

    public void setLastname(String lastname) {this.lastname = lastname;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getTelephone() {return telephone;}

    public void setTelephone(String telephone) {this.telephone = telephone;}

    public List<EnrollmentDTO> getEnrollments() {return enrollments;}

    public void setEnrollments(List<EnrollmentDTO> enrollments) {this.enrollments = enrollments;}
}
