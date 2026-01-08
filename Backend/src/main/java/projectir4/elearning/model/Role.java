package projectir4.elearning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Setter
@Getter
@AllArgsConstructor
@Table(name = "role")
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    private RoleName name;

    public Role() {
    }

}
