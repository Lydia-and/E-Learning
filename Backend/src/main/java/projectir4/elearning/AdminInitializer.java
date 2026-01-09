package projectir4.elearning;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import projectir4.elearning.model.Role;
import projectir4.elearning.model.RoleName;
import projectir4.elearning.model.User;
import projectir4.elearning.repository.RoleRepository;
import projectir4.elearning.repository.UserRepository;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmins(
            UserRepository userRepo,
            RoleRepository roleRepo,
            PasswordEncoder encoder) {

        return args -> {

            String[][] admins = {
                    {"admin1", "password1", "email1"},
                    {"admin2", "password2", "email2"},
                    {"admin3", "password3", "email3"},
                    {"admin4", "password4", "email4"},
                    {"admin5", "password5", "email5"}
            };


            Role adminRole = roleRepo.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            for (String[] a : admins) {
                if (userRepo.findByUsername(a[0]).isEmpty()) {
                    User admin = new User();
                    admin.setUsername(a[0]);
                    admin.setPassword(encoder.encode(a[1]));
                    admin.setEmail(a[2]);
                    //admin.setEnabled(true);
                    admin.getRoles().add(adminRole);
                    userRepo.save(admin);
                }
            }
        };
    }
}