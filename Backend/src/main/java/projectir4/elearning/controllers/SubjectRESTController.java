package projectir4.elearning.controllers;

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

    @RequestMapping(method = RequestMethod.GET)
    public List<SubjectDTO> findAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<SubjectDTO> findSubjectById(@PathVariable Long id){
        Optional<Subject> subject = subjectRepository.findById(id);
        if(subject.isEmpty()){
            System.out.println("Subject not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<> (subjectMapper.toDTO(subject.get()), HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<SubjectDTO> addSubject(@RequestBody SubjectDTO dto) {

        Subject subject = subjectMapper.toEntity(dto);
        List<TeacherCourse> teacherCourses = new ArrayList<>();

        if (dto.getTeacherIds() != null) {
            for (Long teacherId : dto.getTeacherIds()) {
                Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);
                if (teacherOpt.isEmpty()) {
                    System.out.println("Teacher not found");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                Teacher teacher = teacherOpt.get();
                TeacherCourse teacherCourse = new TeacherCourse();
                teacherCourse.setTeacher(teacher);
                teacherCourse.setSubject(subject);
                teacherCourse.setTeacherRole("Default");
                teacherCourses.add(teacherCourse);
            }
        }

        subject.setTeacherCourses(new HashSet<>(teacherCourses));
        Subject saved = subjectRepository.save(subject);
        teacherCourseRepository.saveAll(teacherCourses);
        return new ResponseEntity<>(subjectMapper.toDTO(saved), HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllSubject(){

        subjectRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Subject> deleteSubject(@PathVariable("id") long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if (subject.isEmpty()) {
            System.out.println("Subject not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        subjectRepository.delete(subject.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<List<SubjectDTO>> updateAllSubjects(@RequestBody List<SubjectDTO> subjectDTOs) {
        subjectRepository.deleteAll();

        List<Subject> savedSubjects = new ArrayList<>();

        for (SubjectDTO dto : subjectDTOs) {
            Subject subject = subjectMapper.toEntity(dto);
            List<TeacherCourse> teacherCourses = new ArrayList<>();

            if (dto.getTeacherIds() != null) {
                for (Long teacherId : dto.getTeacherIds()) {
                    Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);
                    if (teacherOpt.isEmpty()) {
                        System.out.println("Teacher not found ");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    Teacher teacher = teacherOpt.get();
                    TeacherCourse tc = new TeacherCourse();
                    tc.setTeacher(teacher);
                    tc.setSubject(subject);
                    tc.setTeacherRole("Default");
                    teacherCourses.add(tc);
                }
            }

            subject.setTeacherCourses(new HashSet<>(teacherCourses));
            savedSubjects.add(subjectRepository.save(subject));
            teacherCourseRepository.saveAll(teacherCourses);
        }

        List<SubjectDTO> response = savedSubjects.stream()
                .map(subjectMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value ="/{id}", method = RequestMethod.PUT)
    public ResponseEntity<SubjectDTO> updateSubject(@RequestBody SubjectDTO dto, @PathVariable("id") long id) {
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        if (optionalSubject.isEmpty()) {
            System.out.println("Subject not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Subject subject = optionalSubject.get();
        subject.setName(dto.getName());
        subject.setCoefficient(dto.getCoefficient());

        // Clear and update teacherCourses
        teacherCourseRepository.deleteAll(subject.getTeacherCourses());
        List<TeacherCourse> teacherCourses = new ArrayList<>();

        if (dto.getTeacherIds() != null) {
            for (Long teacherId : dto.getTeacherIds()) {
                Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);
                if (teacherOpt.isEmpty()) {
                    System.out.println("Teacher not found with ID: " + teacherId);
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                Teacher teacher = teacherOpt.get();
                TeacherCourse teacherCourse = new TeacherCourse();
                teacherCourse.setTeacher(teacher);
                teacherCourse.setSubject(subject);
                teacherCourse.setTeacherRole("Default");
                teacherCourses.add(teacherCourse);
            }
        }

        subject.setTeacherCourses(new HashSet<>(teacherCourses));
        subjectRepository.save(subject);
        teacherCourseRepository.saveAll(teacherCourses);

        return new ResponseEntity<>(subjectMapper.toDTO(subject), HttpStatus.OK);
    }

    @RequestMapping(value ="/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Subject> updatePartOfSubject(@RequestBody Map<String, Object> updates, @PathVariable("id") long id ){
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) {
            System.out.println("Subject not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Subject subject = subjectOpt.get();
        partialUpdate(subject,updates);
        return new ResponseEntity<>(subject, HttpStatus.OK);
    }

    private void partialUpdate(Subject subject, Map<String, Object> updates){
        if(updates.containsKey("name")){
            subject.setName((String) updates.get("name"));
        }
        if(updates.containsKey("coefficient")){
            Number coef = (Number) updates.get("coefficient");
            if(coef != null) {
                subject.setCoefficient(coef.intValue());
            }
        }
        subjectRepository.save(subject);
    }

    //list of teachers
    @GetMapping("/{id}/teachers")
    public ResponseEntity<List<TeacherDTO>> getTeachersForSubject(@PathVariable Long id) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Subject subject = subjectOpt.get();
        List<Teacher> teachers = subject.getTeacherCourses().stream()
                .map(TeacherCourse::getTeacher)
                .distinct()
                .collect(Collectors.toList());

        List<TeacherDTO> teacherDTOs = teachers.stream()
                .map(teacherMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(teacherDTOs, HttpStatus.OK);
    }

    //list of students
    @GetMapping("/{id}/students")
    public ResponseEntity<List<StudentDTO>> getStudentsForSubject(@PathVariable Long id) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Subject subject = subjectOpt.get();
        List<Student> students = subject.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .distinct()
                .collect(Collectors.toList());

        List<StudentDTO> studentDTOs = students.stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(studentDTOs, HttpStatus.OK);
    }
}
