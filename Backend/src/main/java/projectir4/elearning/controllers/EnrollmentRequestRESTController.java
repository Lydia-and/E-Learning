package projectir4.elearning.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import projectir4.elearning.dto.EnrollmentRequestDTO;
import projectir4.elearning.mapper.EnrollmentMapper;
import projectir4.elearning.mapper.EnrollmentRequestMapper;
import projectir4.elearning.model.*;
import projectir4.elearning.repository.EnrollmentRepository;
import projectir4.elearning.repository.EnrollmentRequestRepository;
import projectir4.elearning.repository.StudentRepository;
import projectir4.elearning.repository.TeacherCourseRepository;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/requests")
public class EnrollmentRequestRESTController {

    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final EnrollmentRequestMapper enrollmentRequestMapper;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentRequestRESTController(EnrollmentRequestRepository enrollmentRequestRepository, TeacherCourseRepository teacherCourseRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, EnrollmentRequestMapper enrollmentRequestMapper, EnrollmentMapper enrollmentMapper) {
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentRequestMapper = enrollmentRequestMapper;
        this.enrollmentMapper = enrollmentMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentRequestDTO> createRequest(@RequestBody EnrollmentRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails) {

        Student student = studentRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TeacherCourse course = teacherCourseRepository.findById(dto.getTeacherCourse().getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        //pour evite les double demandes
        boolean exists = enrollmentRequestRepository.existsByStudentAndTeacherCourse(student, course);

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudent(student);
        request.setTeacherCourse(course);
        request.setStatus(RequestStatus.PENDING);

        EnrollmentRequest saved = enrollmentRequestRepository.save(request);
        return new ResponseEntity<>(enrollmentRequestMapper.toDTO(saved), HttpStatus.CREATED);
    }


    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public List<EnrollmentRequestDTO> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        return enrollmentRequestRepository
                .findByStudentUsername(userDetails.getUsername())
                .stream()
                .map(enrollmentRequestMapper::toDTO)
                .toList();
    }


    @GetMapping("/teacher")
    @PreAuthorize(" hasRole('ROLE_ADMIN') or authentication.principal.userId == #id")
    public List<EnrollmentRequestDTO> getRequestsForTeacher(
            @AuthenticationPrincipal UserDetails userDetails,@RequestParam Long id) {

        return enrollmentRequestRepository.findById(id)
                .stream()
                .map(enrollmentRequestMapper::toDTO)
                .toList();
    }


    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> acceptRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        EnrollmentRequest request = enrollmentRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getTeacherCourse()
                .getTeacher()
                .getUsername()
                .equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Not your request");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(request.getStudent());
        enrollment.setSubject(request.getTeacherCourse().getSubject());
        enrollment.setGrade(0);

        enrollmentRepository.save(enrollment);

        request.setStatus(RequestStatus.ACCEPTED);
        enrollmentRequestRepository.save(request);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        EnrollmentRequest request = enrollmentRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getTeacherCourse()
                .getTeacher()
                .getUsername()
                .equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Not your request");
        }

        request.setStatus(RequestStatus.REJECTED);
        enrollmentRequestRepository.save(request);

        return ResponseEntity.ok().build();
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<EnrollmentRequestDTO> findAll() {
        return enrollmentRequestRepository.findAll()
                .stream()
                .map(enrollmentRequestMapper::toDTO)
                .toList();
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        EnrollmentRequest request = enrollmentRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        enrollmentRequestRepository.delete(request);
        return ResponseEntity.noContent().build();
    }
}

