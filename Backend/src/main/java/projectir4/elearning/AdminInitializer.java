package projectir4.elearning;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import projectir4.elearning.repository.RoleRepository;
import projectir4.elearning.repository.UserRepository;

public class AdminInitializer {

    CommandLineRunner initAdmins(
            UserRepository userRepo,
            RoleRepository roleRepo,
            PasswordEncoder encoder) {

        return args -> {

            String[][] admins = {
                {"admin1", "password1"},
                {"admin2", "password2"},
                {"admin3", "password3"},
                {"admin4", "password4"},
                {"admin5", "password5"}
            };

            Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            for (String[] a : admins) {
                if (userRepo.findByUsername(a[0]).isEmpty()) {
                    User admin = new User();
                    admin.setUsername(a[0]);
                    admin.setPassword(encoder.encode(a[1]));
                    admin.setEnabled(true);
                    admin.getRoles().add(adminRole);
                    userRepo.save(admin);
                }
            }
        };
    }
}
