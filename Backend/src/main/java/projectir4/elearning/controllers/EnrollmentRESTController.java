package projectir4.elearning.controllers;

import projectir4.elearning.dto.EnrollmentDTO;
import projectir4.elearning.mapper.EnrollmentMapper;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentRESTController {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    private final EnrollmentMapper enrollmentMapper;

    @Autowired
    public EnrollmentRESTController(StudentRepository studentRepository,
                                    EnrollmentRepository enrollmentRepository,
                                    SubjectRepository subjectRepository,
                                    EnrollmentMapper enrollmentMapper) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.subjectRepository = subjectRepository;

        this.enrollmentMapper = enrollmentMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<EnrollmentDTO> findAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .filter(enrollment -> enrollment.getStudent() != null && enrollment.getSubject() != null)
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<EnrollmentDTO> findEnrollmentById(@PathVariable Long id) {
        Optional<Enrollment> enrollment = enrollmentRepository.findById(id);

        if (enrollment.isEmpty()) {
            System.out.println("Enrollment not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (enrollment.get().getSubject() == null || enrollment.get().getStudent() == null) {
            enrollmentRepository.delete(enrollment.get());
            System.out.println("The enrollment was empty and so deleted!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(enrollmentMapper.toDTO(enrollment.get()), HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<EnrollmentDTO> addEnrollment(@RequestBody EnrollmentDTO dto) {
        //to avoid repetition
        Optional<Student> studentOpt = studentRepository.findById(dto.getStudentId());
        Optional<Subject> subjectOpt = subjectRepository.findById(dto.getSubjectId());

        if (studentOpt.isEmpty() || subjectOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Enrollment enrollment = enrollmentMapper.toEntity(dto, studentOpt.get(), subjectOpt.get());
        Enrollment saved = enrollmentRepository.save(enrollment);

        return new ResponseEntity<>(enrollmentMapper.toDTO(saved), HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllEnrollment(){
        enrollmentRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Enrollment> deleteEnrollment(@PathVariable("id") long id) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);

        if (enrollmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        enrollmentRepository.delete(enrollmentOpt.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value ="/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<EnrollmentDTO> updatePartOfEnrollment(@RequestBody Map<String, Object> updates, @PathVariable("id") long id ){
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);

        if (enrollmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Enrollment enrollment = enrollmentOpt.get();
        partialUpdate(enrollment, updates);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return new ResponseEntity<>(enrollmentMapper.toDTO(saved), HttpStatus.OK);
    }

    private void partialUpdate(Enrollment enrollment, Map<String, Object> updates){
        if(updates.containsKey("student")) {
            Student student = enrollment.getStudent();
            if (student != null) {
                Map<String, Object> studentUpdates = (Map<String, Object>) updates.get("student");
                if (studentUpdates.containsKey("firstname")) {
                    student.setFirstname((String) studentUpdates.get("firstname"));
                }
                if (studentUpdates.containsKey("lastname")) {
                    student.setLastname((String) studentUpdates.get("lastname"));
                }
                if (studentUpdates.containsKey("telephone")) {
                    student.setTelephone((String) studentUpdates.get("telephone"));
                }
                if (studentUpdates.containsKey("email")) {
                    student.setEmail((String) studentUpdates.get("email"));
                }
                studentRepository.save(student);
            }
        }
        if(updates.containsKey("subject")) {
            Subject subject = enrollment.getSubject();
            if (subject != null) {
                Map<String, Object> subjectUpdates = (Map<String, Object>) updates.get("subject");
                if (subjectUpdates.containsKey("name")) {
                    subject.setName((String) subjectUpdates.get("name"));
                }
                if (subjectUpdates.containsKey("coefficient")) {
                    subject.setCoefficient((int) subjectUpdates.get("coefficient"));
                }
                subjectRepository.save(subject);
            }
        }

        if(updates.containsKey("grade")){
            //just in case it's not a double that we have being given
            Object gradeObj = updates.get("grade");
            if (gradeObj instanceof Number) {
                enrollment.setGrade(((Number) gradeObj).doubleValue());
            }
        }

        enrollmentRepository.save(enrollment);
    }
}
/*
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