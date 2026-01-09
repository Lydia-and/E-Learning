package projectir4.elearning.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import projectir4.elearning.dto.EnrollmentDTO;
import projectir4.elearning.mapper.EnrollmentMapper;
import projectir4.elearning.model.Enrollment;
import projectir4.elearning.model.Student;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollments")
@CrossOrigin(origins = "http://localhost:4200")
public class EnrollmentRESTController {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Autowired
    public EnrollmentRESTController(StudentRepository studentRepository,
                                    EnrollmentRepository enrollmentRepository,
                                    SubjectRepository subjectRepository,
                                    TeacherCourseRepository teacherCourseRepository,
                                    EnrollmentMapper enrollmentMapper,
                                    TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.subjectRepository = subjectRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.enrollmentMapper = enrollmentMapper;
        this.teacherRepository = teacherRepository;
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'TEACHER')")
    @GetMapping
    public List<EnrollmentDTO> findAllEnrollments(Authentication authentication) {
        String username = authentication.getName();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return enrollmentRepository.findAll().stream()
                    .map(enrollmentMapper::toDTO)
                    .collect(Collectors.toList());
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            Student student = studentRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            return enrollmentRepository.findAllByStudentId(student.getId()).stream()
                    .map(enrollmentMapper::toDTO)
                    .collect(Collectors.toList());
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            Teacher teacher = teacherRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            List<Long> subjectIds = teacher.getTeacherCourses().stream()
                    .map(tc -> tc.getSubject().getId())
                    .toList();

            return enrollmentRepository.findAllBySubjectIdIn(subjectIds).stream()
                    .map(enrollmentMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }


    @PreAuthorize("hasAnyRole('ADMIN','TEACHER') or authentication.principal.userId == #id")
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> findEnrollmentById(@PathVariable Long id, Authentication authentication) {

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);
        if (enrollmentOpt.equals("")) {
            System.out.println("Enrollment not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Enrollment enrollment = enrollmentOpt.get();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            String username = authentication.getName();
            boolean ownsCourse = enrollment.getSubject().getTeacherCourses().stream()
                    .anyMatch(teach -> teach.getTeacher().getEmail().equals(username));
            if (!ownsCourse) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(enrollmentMapper.toDTO(enrollment));
    }


    @PreAuthorize(" @StudentRepository.findById(#id).orElseThrow().getUser().getId() == authentication.principal.userId")
    @PostMapping
    public ResponseEntity<EnrollmentDTO> addEnrollment(@RequestBody EnrollmentDTO dto, Authentication authentication) {
        String username = authentication.getName();
        Student student = studentRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Subject subject = subjectRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Enrollment enrollment = enrollmentMapper.toEntity(dto, student, subject);
        Enrollment saved = enrollmentRepository.save(enrollment);

        return new ResponseEntity<>(enrollmentMapper.toDTO(saved), HttpStatus.CREATED);
    }


    //a voir si je faire userRepo ou StudentRepo, selon si avec user le prof peut delte que ses enrollment ou ceux des autres aussi
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER') or @UserRepository.findById(#id).orElseThrow().getUser().getId() == authentication.principal.userId")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id, Authentication authentication) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);

        if (enrollmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Enrollment enrollment = enrollmentOpt.get();

        //students can delete only their own
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            if (!enrollment.getStudent().getEmail().equals(authentication.getName())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        //teacher can delete only enrollments of their own subjetcs
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            boolean ownsCourse = enrollment.getSubject().getTeacherCourses().stream()
                    .anyMatch(tc -> tc.getTeacher().getEmail().equals(authentication.getName()));
            if (!ownsCourse) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        enrollmentRepository.delete(enrollment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PreAuthorize("hasRole('ADMIN') or @UserRepository.findById(#id).orElseThrow().getUser().getId() == authentication.principal.userId")
    @PatchMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> updatePartOfEnrollment(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);

        if (enrollmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Enrollment enrollment = enrollmentOpt.get();
        partialUpdate(enrollment, updates);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return new ResponseEntity<>(enrollmentMapper.toDTO(saved), HttpStatus.OK);
    }

    private void partialUpdate(Enrollment enrollment, Map<String, Object> updates) {
        if (updates.containsKey("grade")) {
            Object gradeObj = updates.get("grade");
            if (gradeObj instanceof Number) {
                enrollment.setGrade(((Number) gradeObj).doubleValue());
            }
        }
    }
}

/* to test on postman
{
    {
    "grade": 4.0,
    "student": {
        "firstname": "jess",
        "lastname": "anna",
        "email": "jessAnna",
        "telephone": "0669"
    },
    "subject": {
        "name": "SE",
        "coefficient": 5.0
    }
},
{
    "student": {
        "firstname": "jamila",
        "lastname": "hatatache",
        "email": "tata",
        "telephone": "008"
    },
    "subject": {
        "name": "newSub",
        "coefficient": 4
    },
    "grade": 3.0
}
}
 */