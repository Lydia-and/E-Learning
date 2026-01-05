package projectir4.elearning.controllers;

import projectir4.elearning.dto.TeacherCourseDTO;
import projectir4.elearning.dto.TeacherDTO;
import projectir4.elearning.mapper.TeacherCourseMapper;
import projectir4.elearning.mapper.TeacherMapper;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;
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
    private final SubjectRepository subjectRepository;

    private final TeacherMapper teacherMapper;
    private final TeacherCourseMapper teacherCourseMapper;

    @Autowired
    public TeacherRESTController(TeacherRepository teacherRepository,
                                 TeacherCourseRepository teacherCourseRepository,
                                 SubjectRepository subjectRepository,
                                 TeacherMapper teacherMapper,
                                 TeacherCourseMapper teacherCourseMapper) {
        this.teacherRepository = teacherRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.subjectRepository = subjectRepository;

        this.teacherMapper = teacherMapper;
        this.teacherCourseMapper = teacherCourseMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<TeacherDTO> findAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<TeacherDTO> findTeacherById(@PathVariable Long id){
        Optional<Teacher> teacher = teacherRepository.findById(id);
        if(teacher.isEmpty()){
            System.out.println("Teacher not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<> (teacherMapper.toDTO(teacher.get()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<TeacherDTO> addTeacher(@RequestBody TeacherDTO teacherDTO) {
        Teacher teacher = teacherMapper.toEntity(teacherDTO);
        List<TeacherCourse> teacherCourses = new ArrayList<>();

        if (teacherDTO.getTeacherCourses() != null) {
            for (TeacherCourseDTO tcDTO : teacherDTO.getTeacherCourses()) {
                Subject subject = subjectRepository.findById(tcDTO.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("Subject not found"));
                TeacherCourse newCourse = new TeacherCourse();
                newCourse.setTeacher(teacher);
                newCourse.setSubject(subject);
                newCourse.setTeacherRole(tcDTO.getRole());
                teacherCourses.add(newCourse);
            }
        }

        teacher.setTeacherCourses(teacherCourses);
        Teacher saved = teacherRepository.save(teacher);
        //teacherCourseRepository.saveAll(teacherCourses);

        return new ResponseEntity<>(teacherMapper.toDTO(saved), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<Void> deleteAllTeachers(){
        teacherRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

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

    //supprime aussi
    @PutMapping
    @Transactional
    public ResponseEntity<List<TeacherDTO>> updateAllTeachers(@RequestBody List<TeacherDTO> teacherDTOs) {
        teacherRepository.deleteAll();
        List<Teacher> teachers = new ArrayList<>();

        for (TeacherDTO dto : teacherDTOs) {
            Teacher teacher = teacherMapper.toEntity(dto);

            List<TeacherCourse> teacherCourses = new ArrayList<>();
            if (dto.getTeacherCourses() != null) {
                for (TeacherCourseDTO tcDTO : dto.getTeacherCourses()) {
                    Subject subject = subjectRepository.findById(tcDTO.getSubjectId())
                            .orElseThrow(() -> new RuntimeException("Subject not found"));
                    TeacherCourse newCourse = new TeacherCourse();
                    newCourse.setTeacher(teacher);
                    newCourse.setSubject(subject);
                    newCourse.setTeacherRole(tcDTO.getRole());
                    teacherCourses.add(newCourse);
                }
            }
            teacher.setTeacherCourses(teacherCourses);
            teachers.add(teacher);
        }

        List<Teacher> savedTeachers = teacherRepository.saveAll(teachers);

        List<TeacherDTO> response = savedTeachers.stream()
                .map(teacherMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //supprime
    @RequestMapping(value ="/{id}", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<TeacherDTO> updateTeacher(@RequestBody TeacherDTO updatedDTO, @PathVariable("id") long id) {
        Optional<Teacher> teacherOpt = teacherRepository.findById(id);
        if (teacherOpt.isEmpty()) {
            System.out.println("Teacher not found !");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Teacher teacher = teacherOpt.get();
        teacher.setTeacherFirstName(updatedDTO.getFirstname());
        teacher.setTeacherLastName(updatedDTO.getLastname());
        teacher.setEmail(updatedDTO.getEmail());
        teacher.setNumber(updatedDTO.getNumber());

        List<TeacherCourse> updatedCourses = new ArrayList<>();
        if (updatedDTO.getTeacherCourses() != null) {
            for (TeacherCourseDTO teacherCourseDTO : updatedDTO.getTeacherCourses()) {
                Subject subject = subjectRepository.findById(teacherCourseDTO.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("Subject not found"));

                TeacherCourse course = new TeacherCourse();

                course.setId(teacherCourseDTO.getId());
                course.setTeacher(teacher);
                course.setSubject(subject);
                course.setTeacherRole(teacherCourseDTO.getRole());

                updatedCourses.add(course);
            }
        }

        teacher.getTeacherCourses().clear();
        teacher.getTeacherCourses().addAll(updatedCourses);

        Teacher saved = teacherRepository.save(teacher);
        //teacherCourseRepository.saveAll(updatedCourses);

        return new ResponseEntity<>(teacherMapper.toDTO(saved), HttpStatus.OK);
    }

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
        if(updates.containsKey("teacherFirstName")){
            teacher.setTeacherFirstName((String) updates.get("teacherFirstName"));
        }

        if(updates.containsKey("teacherLastName")){
            teacher.setTeacherLastName((String) updates.get("teacherLastName"));
        }

        if(updates.containsKey("email")){
            teacher.setEmail((String) updates.get("email"));
        }

        if(updates.containsKey("number")){
            teacher.setNumber((String) updates.get("number"));
        }

        teacherRepository.save(teacher);
    }
}
