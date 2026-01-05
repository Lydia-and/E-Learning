package projectir4.elearning.controllers;

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
    private final SubjectRepository subjectRepository;

    private final StudentMapper studentMapper;
    private final EnrollmentMapper enrollmentMapper;

    @Autowired
    public StudentRESTController(StudentRepository studentRepository,
                                 EnrollmentRepository enrollmentRepository,
                                 SubjectRepository subjectRepository,
                                 StudentMapper studentMapper,
                                 EnrollmentMapper enrollmentMapper) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.subjectRepository = subjectRepository;

        this.studentMapper = studentMapper;
        this.enrollmentMapper = enrollmentMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<StudentDTO>> findAllStudents(){
        List<StudentDTO> dtos = studentRepository.findAll().stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<StudentDTO> findStudentById(@PathVariable Long id){
        return studentRepository.findById(id)
                .map(student -> ResponseEntity.ok(studentMapper.toDTO(student)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @RequestMapping(method = RequestMethod.POST)
    //@PostMapping
    public ResponseEntity<StudentDTO> addStudent(@RequestBody StudentDTO dto) {
        Student student = studentMapper.toEntity(dto);

        //so that we can use its id
        Student savedStudent = studentRepository.save(student);

        List<Enrollment> enrollments = new ArrayList<>();

        if (dto.getEnrollments() != null) {
            for (EnrollmentDTO eDTO : dto.getEnrollments()) {
                Subject subject = subjectRepository.findById(eDTO.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("Subject not found"));
                Enrollment enrollment = enrollmentMapper.toEntity(eDTO, savedStudent, subject);
                enrollments.add(enrollment);
            }
        }

        savedStudent.setEnrollments(enrollments);
        enrollmentRepository.saveAll(enrollments);

        return new ResponseEntity<>(studentMapper.toDTO(savedStudent), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllStudents(){
        studentRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

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

    //supprime
    @RequestMapping(value ="/{id}", method = RequestMethod.PUT)
    public ResponseEntity<StudentDTO> updateStudent(@RequestBody StudentDTO dto,  @PathVariable("id") long id){

        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            System.out.println("Student not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Student student = studentOpt.get();
        student.setFirstname(dto.getFirstname());
        student.setLastname(dto.getLastname());
        student.setEmail(dto.getEmail());
        student.setTelephone(dto.getTelephone());

        student.getEnrollments().clear();
        List<Enrollment> enrollments = new ArrayList<>();

        if (dto.getEnrollments() != null) {
            for (EnrollmentDTO eDTO : dto.getEnrollments()) {
                Optional<Subject> subjectOpt = subjectRepository.findById(eDTO.getSubjectId());
                if (subjectOpt.isEmpty()) {
                    System.out.println("Subject not found for ID: " + eDTO.getSubjectId());
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                Subject subject = subjectOpt.get();
                Enrollment enrollment = enrollmentMapper.toEntity(eDTO, student, subject);
                enrollments.add(enrollment);
            }
        }
        student.getEnrollments().addAll(enrollments);
        Student updatedStudent = studentRepository.save(student);

        StudentDTO responseDto = studentMapper.toDTO(updatedStudent);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

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
        if(updates.containsKey("firstname")){
            student.setFirstname((String) updates.get("firstname"));
        }

        if(updates.containsKey("lastname")){
            student.setLastname((String) updates.get("lastname"));
        }

        if(updates.containsKey("email")){
            student.setEmail((String) updates.get("email"));
        }

        if(updates.containsKey("telephone")){
            student.setTelephone((String) updates.get("telephone"));
        }


        studentRepository.save(student);
    }
}
