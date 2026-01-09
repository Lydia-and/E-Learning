package projectir4.elearning.controllers;

import projectir4.elearning.message.request.LoginForm;
import projectir4.elearning.message.request.SignUpForm;
import projectir4.elearning.message.response.JwtResponse;
import projectir4.elearning.message.response.ResponseMessage;
import projectir4.elearning.model.*;
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
@CrossOrigin(origins="http://localhost:4200", maxAge =3600)
@RequestMapping("/auth")
public class AuthRESTController {

    private DaoAuthenticationProvider daoAuthenticationProvider;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;

    @Autowired
    public AuthRESTController(DaoAuthenticationProvider daoAuthenticationProvider,
                              UserRepository userRepository, RoleRepository roleRepository,
                              PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;

    }

    @PostMapping(value ="/login", produces = "application/json")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest){
        Authentication authentication = daoAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping(value ="/signup",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest){
        if (userRepository.existsByUsername(signUpRequest.getUsername())){
            return new ResponseEntity <>(new ResponseMessage("Invalid username or password. Please try again"), HttpStatus.BAD_REQUEST);
        }
        String password = passwordEncoder.encode(signUpRequest.getPassword());
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), password);


        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();


        strRoles.forEach(role -> {
            if(role.equals("teacher")) {
                Role teacherRole = roleRepository.findByName(RoleName.ROLE_TEACHER)
                        .orElseThrow(() -> new RuntimeException("Role Teacher not found."));
                roles.add(teacherRole);
            }else if(role.equals("user")) {
                        Role studentRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role User not found."));
                        roles.add(studentRole);
            }
        });

        //user.setUsername(signUpRequest.getUsername());
        //user.setEmail(signUpRequest.getEmail());
        //user.setPassword(password);
        user.setRoles(roles);

        userRepository.save(user);
        return new ResponseEntity<> (new ResponseMessage("User registered successfully."), HttpStatus.OK);
    }

}
