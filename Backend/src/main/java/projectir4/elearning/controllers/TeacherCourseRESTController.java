package projectir4.elearning.controllers;

import projectir4.elearning.dto.TeacherCourseDTO;
import projectir4.elearning.mapper.TeacherCourseMapper;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;
import projectir4.elearning.repository.*;
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
@RequestMapping("/teacherCourses")
public class TeacherCourseRESTController {

    private final TeacherCourseRepository teacherCourseRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    private final TeacherCourseMapper teacherCourseMapper;

    @Autowired
    public TeacherCourseRESTController(TeacherRepository teacherRepository,
                                       TeacherCourseRepository teacherCourseRepository,
                                       SubjectRepository subjectRepository,
                                       TeacherCourseMapper teacherCourseMapper) {
        this.teacherRepository = teacherRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.subjectRepository = subjectRepository;

        this.teacherCourseMapper = teacherCourseMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<TeacherCourseDTO> findAllTeacherCourses() {
        return teacherCourseRepository.findAll().stream()
                .filter(tc -> tc.getTeacher() != null && tc.getSubject() != null)
                .map(teacherCourseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<TeacherCourseDTO> findTeacherCourseById(@PathVariable Long id){
        Optional<TeacherCourse> teacherCourse = teacherCourseRepository.findById(id);
        if(teacherCourse.isEmpty()){
            System.out.println("Teacher-Course not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //supprimer et pas montrer si vide
        if(teacherCourse.get().getSubject()==null && teacherCourse.get().getTeacher()==null){
            teacherCourseRepository.delete(teacherCourse.get());
            System.out.println("The teacher-Course was empty and so deleted!");
        }
        return new ResponseEntity<> ( teacherCourseMapper.toDTO(teacherCourse.get()), HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TeacherCourseDTO> addTeacherCourse(@RequestBody TeacherCourseDTO dto) {
        Optional<Teacher> teacherOpt = teacherRepository.findById(dto.getTeacherId());
        Optional<Subject> subjectOpt = subjectRepository.findById(dto.getSubjectId());

        if (teacherOpt.isEmpty() || subjectOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        TeacherCourse teacherCourse = teacherCourseMapper.toEntity(dto, teacherOpt.get(), subjectOpt.get());
        TeacherCourse saved = teacherCourseRepository.save(teacherCourse);

        return new ResponseEntity<>(teacherCourseMapper.toDTO(saved), HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllTeacherCourse(){
        List<Teacher> teachers = teacherRepository.findAll();
        List<Subject> subjects = subjectRepository.findAll();

        for (Teacher teacher : teachers) {
            List<TeacherCourse> teacherCourses = teacher.getTeacherCourses();
            if (teacherCourses != null && !teacherCourses.isEmpty()) {
                teacher.setTeacherCourses(null);
                teacherRepository.save(teacher);
            }
        }


        for (Subject subject : subjects) {
            List<TeacherCourse> teacherCourses = new ArrayList<>(subject.getTeacherCourses());
            if (teacherCourses != null && !teacherCourses.isEmpty()) {
                subject.setTeacherCourses(null);
                subjectRepository.save(subject);
            }
        }
        teacherCourseRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TeacherCourse> deleteTeacherCourse(@PathVariable("id") long id) {
        Optional<TeacherCourse> teacherCourseOpt = teacherCourseRepository.findById(id);

        if (teacherCourseOpt.isEmpty()) {
            return new ResponseEntity<TeacherCourse>(HttpStatus.NOT_FOUND);
        }

        TeacherCourse teacherCourse = teacherCourseOpt.get();

        Teacher teacher = teacherCourse.getTeacher();
        if (teacher != null) {
            teacher.getTeacherCourses().remove(teacherCourse);
            teacherRepository.save(teacher);
        }

        Subject subject = teacherCourse.getSubject();
        if (subject != null) {
            subject.getTeacherCourses().remove(teacherCourse);
            subjectRepository.save(subject);
        }

        teacherCourseRepository.delete(teacherCourse);

        return new ResponseEntity<TeacherCourse>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value ="/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<TeacherCourseDTO> updatePartOfTeacherCourse(@RequestBody Map<String, Object> updates, @PathVariable("id") long id ){
        Optional<TeacherCourse> teacherCourseOpt = teacherCourseRepository.findById(id);

        if (teacherCourseOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        TeacherCourse teacherCourse = teacherCourseOpt.get();
        partialUpdate(teacherCourse, updates);

        TeacherCourse saved = teacherCourseRepository.save(teacherCourse);
        return new ResponseEntity<>(teacherCourseMapper.toDTO(saved), HttpStatus.OK);
    }

    private void partialUpdate(TeacherCourse teacherCourse, Map<String, Object> updates){
        if(updates.containsKey("teacher")) {
            Teacher teacher = teacherCourse.getTeacher();
            if (teacher != null) {
                Map<String, Object> teacherUpdates = (Map<String, Object>) updates.get("teacher");
                if (teacherUpdates.containsKey("teacherFirstName")) {
                    teacher.setTeacherFirstName((String) teacherUpdates.get("teacherFirstName"));
                }
                if (teacherUpdates.containsKey("teacherLastName")) {
                    teacher.setTeacherLastName((String) teacherUpdates.get("teacherLastName"));
                }
                if (teacherUpdates.containsKey("number")) {
                    teacher.setNumber((String) teacherUpdates.get("number"));
                }
                if (teacherUpdates.containsKey("email")) {
                    teacher.setEmail((String) teacherUpdates.get("email"));
                }
                teacherRepository.save(teacher);
            }
        }
        if(updates.containsKey("subject")) {
            Subject subject = teacherCourse.getSubject();
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

        if(updates.containsKey("teacherRole")){
            teacherCourse.setTeacherRole((String) updates.get("teacherRole"));
        }

        teacherCourseRepository.save(teacherCourse);
    }
}
