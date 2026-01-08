package projectir4.elearning.controllers;

import projectir4.elearning.message.request.LoginForm;
import projectir4.elearning.message.request.SignUpForm;
import projectir4.elearning.message.response.JwtResponse;
import projectir4.elearning.message.response.ResponseMessage;
import projectir4.elearning.model.Role;
import projectir4.elearning.model.RoleName;
import projectir4.elearning.model.User;
import projectir4.elearning.repository.RoleRepository;
import projectir4.elearning.repository.UserRepository;
import projectir4.elearning.security.jwt.JwtProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/auth")
public class AuthRESTController {

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthRESTController(DaoAuthenticationProvider daoAuthenticationProvider,
                              UserRepository userRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder passwordEncoder,
                              JwtProvider jwtProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    // ===================== SIGN IN =====================
    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = daoAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(
                new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities())
        );
    }

    // ===================== SIGN UP =====================
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Fail -> Username is already taken."));
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        Set<Role> roles = new HashSet<>();
        Set<String> strRoles = signUpRequest.getRole();

        // Si aucun rôle n'est fourni → USER par défaut
        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role USER not found."));
            roles.add(userRole);
        } else {

            for (String role : strRoles) {

                switch (role.toLowerCase()) {

                    case "teacher":
                        if (!"SECRET123".equals(signUpRequest.getSecretCode())) {
                            return ResponseEntity
                                    .status(HttpStatus.FORBIDDEN)
                                    .body(new ResponseMessage("Teacher code incorrect."));
                        }

                        Role teacherRole = roleRepository.findByName(RoleName.ROLE_TEACHER)
                                .orElseThrow(() -> new RuntimeException("Role TEACHER not found."));
                        roles.add(teacherRole);
                        break;

                    case "admin":
                        if (!"ADMIN123".equals(signUpRequest.getSecretCode())) {
                            return ResponseEntity
                                    .status(HttpStatus.FORBIDDEN)
                                    .body(new ResponseMessage("Admin code incorrect."));
                        }

                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role ADMIN not found."));
                        roles.add(adminRole);
                        break;

                    case "user":
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role USER not found."));
                        roles.add(userRole);
                        break;
                }
            }
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("User registered successfully."));
    }
}
