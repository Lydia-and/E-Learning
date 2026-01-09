package projectir4.elearning.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import projectir4.elearning.dto.StudentDTO;
import projectir4.elearning.dto.SubjectDTO;
import projectir4.elearning.dto.TeacherDTO;
import projectir4.elearning.mapper.StudentMapper;
import projectir4.elearning.mapper.SubjectMapper;
import projectir4.elearning.mapper.TeacherMapper;
import projectir4.elearning.model.*;
import projectir4.elearning.repository.SubjectRepository;
import projectir4.elearning.repository.TeacherCourseRepository;
import projectir4.elearning.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subjects")
public class SubjectRESTController {

    private final SubjectRepository subjectRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectMapper subjectMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;

    @Autowired
    public SubjectRESTController(SubjectRepository subjectRepository,
                                 TeacherCourseRepository teacherCourseRepository,
                                 TeacherRepository teacherRepository,
                                 SubjectMapper subjectMapper,
                                 TeacherMapper teacherMapper,
                                 StudentMapper studentMapper) {
        this.subjectRepository = subjectRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.teacherRepository = teacherRepository;
        this.subjectMapper = subjectMapper;
        this.teacherMapper = teacherMapper;
        this.studentMapper = studentMapper;
    }


    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping
    public List<SubjectDTO> findAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toDTO)
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> findSubjectById(@PathVariable Long id) {
        return subjectRepository.findById(id)
                .map(subject -> new ResponseEntity<>(subjectMapper.toDTO(subject), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SubjectDTO> addSubject(@RequestBody SubjectDTO dto) {
        Subject subject = subjectMapper.toEntity(dto);
        subjectRepository.save(subject);
        return new ResponseEntity<>(subjectMapper.toDTO(subject), HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        if (optionalSubject.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Subject subject = optionalSubject.get();
        subject.setName(dto.getName());
        subject.setCoefficient(dto.getCoefficient());
        subjectRepository.save(subject);

        return new ResponseEntity<>(subjectMapper.toDTO(subject), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if (subject.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        subjectRepository.delete(subject.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{id}/teachers")
    public ResponseEntity<List<TeacherDTO>> getTeachersForSubject(@PathVariable Long id) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<TeacherDTO> teachers = subjectOpt.get().getTeacherCourses().stream()
                .map(TeacherCourse::getTeacher)
                .distinct()
                .map(teacherMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{id}/students")
    public ResponseEntity<List<StudentDTO>> getStudentsForSubject(@PathVariable Long id, Authentication auth) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Subject subject = subjectOpt.get();
        List<Student> students = new ArrayList<>();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            students = subject.getEnrollments().stream()
                    .map(Enrollment::getStudent)
                    .distinct()
                    .toList();
        } else {
            String username = auth.getName();
            Optional<Teacher> teacherOpt = teacherRepository.findByEmail(auth.getClass().getSimpleName());
            if (teacherOpt.isEmpty()) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            Teacher teacher = teacherOpt.get();

            boolean teachesThisCourse = teacher.getTeacherCourses().stream()
                    .anyMatch(tc -> tc.getSubject().getId()==id);

            if (!teachesThisCourse) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            students = subject.getEnrollments().stream()
                    .map(Enrollment::getStudent)
                    .distinct()
                    .toList();
        }

        List<StudentDTO> studentDTOs = students.stream()
                .map(studentMapper::toDTO)
                .toList();

        return new ResponseEntity<>(studentDTOs, HttpStatus.OK);
    }
}

