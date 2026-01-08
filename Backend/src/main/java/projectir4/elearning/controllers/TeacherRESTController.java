package projectir4.elearning.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import projectir4.elearning.dto.EnrollmentRequestDTO;
import projectir4.elearning.dto.TeacherCourseDTO;
import projectir4.elearning.dto.TeacherDTO;
import projectir4.elearning.mapper.EnrollmentMapper;
import projectir4.elearning.mapper.EnrollmentRequestMapper;
import projectir4.elearning.mapper.TeacherCourseMapper;
import projectir4.elearning.mapper.TeacherMapper;
import projectir4.elearning.model.*;
import projectir4.elearning.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teachers")
public class TeacherRESTController {

    private final TeacherRepository teacherRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentRequestRepository enrollmentRequestRepository;

    private final TeacherMapper teacherMapper;
    private final TeacherCourseMapper teacherCourseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final EnrollmentRequestMapper enrollmentRequestMapper;

    @Autowired
    public TeacherRESTController(TeacherRepository teacherRepository,
                                 TeacherCourseRepository teacherCourseRepository,
                                 TeacherMapper teacherMapper,
                                 TeacherCourseMapper teacherCourseMapper,
                                 EnrollmentRepository enrollmentRepository,
                                 EnrollmentRequestRepository enrollmentRequestRepository,
                                 EnrollmentRequestMapper enrollmentRequestMapper,
                                 EnrollmentMapper enrollmentMapper) {

        this.teacherRepository = teacherRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.enrollmentRequestMapper = enrollmentRequestMapper;
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.enrollmentMapper = enrollmentMapper;
        this.teacherMapper = teacherMapper;
        this.teacherCourseMapper = teacherCourseMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.GET)
    public List<TeacherDTO> findAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<TeacherDTO> findTeacherById(@PathVariable Long id){
        Optional<Teacher> teacher = teacherRepository.findById(id);
        if(teacher.isEmpty()){
            System.out.println("Teacher not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<> (teacherMapper.toDTO(teacher.get()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<Void> deleteAllTeachers(){
        teacherRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<Teacher> deleteTeacher(@PathVariable("id") long id) {
        Optional<Teacher> teacher = teacherRepository.findById(id);
        if(teacher.isEmpty()){
            System.out.println("Teacher not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        teacherRepository.delete(teacher.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/me/courses/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> removeStudentFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        TeacherCourse course = teacherCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacher().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Not your course");
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndSubjectId(studentId, course.getSubject().getId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> removeStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        TeacherCourse course = teacherCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacher().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Not your course");
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndSubjectId(studentId, course.getSubject().getId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value ="/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Teacher> updatePartOfTeacher(@RequestBody Map<String, Object> updates, @PathVariable("id") long id ){
        Optional<Teacher> teacherOpt = teacherRepository.findById(id);

        if(teacherOpt.isEmpty()){
            System.out.println("Teacher not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Teacher teacher = teacherOpt.get();
        partialUpdate(teacher,updates);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void partialUpdate(Teacher teacher, Map<String, Object> updates){
        if(updates.containsKey("username")){
            teacher.setUsername((String) updates.get("username"));
        }

        if(updates.containsKey("email")){
            teacher.setEmail((String) updates.get("email"));
        }

        teacherRepository.save(teacher);
    }
}
