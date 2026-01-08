package projectir4.elearning.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import projectir4.elearning.dto.EnrollmentDTO;
import projectir4.elearning.dto.StudentDTO;
import projectir4.elearning.mapper.EnrollmentMapper;
import projectir4.elearning.mapper.StudentMapper;
import projectir4.elearning.model.Enrollment;
import projectir4.elearning.model.Student;
import projectir4.elearning.model.Subject;
import projectir4.elearning.repository.EnrollmentRepository;
import projectir4.elearning.repository.StudentRepository;
import projectir4.elearning.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins ="http://localhost:4200")
@RequestMapping("/students")
public class StudentRESTController {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final StudentMapper studentMapper;
    private final EnrollmentMapper enrollmentMapper;

    @Autowired
    public StudentRESTController(StudentRepository studentRepository,
                                 EnrollmentRepository enrollmentRepository,
                                 StudentMapper studentMapper,
                                 EnrollmentMapper enrollmentMapper) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;

        this.studentMapper = studentMapper;
        this.enrollmentMapper = enrollmentMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<StudentDTO>> findAllStudents(){
        List<StudentDTO> dtos = studentRepository.findAll().stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<StudentDTO> findStudentById(@PathVariable Long id){
        return studentRepository.findById(id)
                .map(student -> ResponseEntity.ok(studentMapper.toDTO(student)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @GetMapping("/me/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public List<EnrollmentDTO> getMyEnrollments(
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = studentRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrollmentRepository.findByStudent(student)
                .stream()
                .map(enrollmentMapper::toDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllStudents(){
        studentRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Student> deleteStudent(@PathVariable("id") long id) {
        Optional<Student> student = studentRepository.findById(id);
        if(student.isEmpty()){
            System.out.println("Student not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        studentRepository.delete(student.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @RequestMapping(value ="/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Student> updatePartOfStudent(@RequestBody Map<String, Object> updates, @PathVariable("id") long id ){
        Optional<Student> studentOpt = studentRepository.findById(id);

        if(studentOpt.isEmpty()){
            System.out.println("Student not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Student student = studentOpt.get();
        partialUpdate(student,updates);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    private void partialUpdate(Student student, Map<String, Object> updates){
        if(updates.containsKey("username")){
            student.setUsername((String) updates.get("username"));
        }

        if(updates.containsKey("email")){
            student.setEmail((String) updates.get("email"));
        }

        studentRepository.save(student);
    }
}
