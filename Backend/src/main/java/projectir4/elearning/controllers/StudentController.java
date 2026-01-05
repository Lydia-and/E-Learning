package projectir4.elearning.controllers;

import projectir4.elearning.model.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StudentController {
    @RequestMapping("/student")
    public String student(Model model) {
        model.addAttribute(  "message", "Simple String from StudentController.");
        Student newStudent = new Student();
        model.addAttribute(  "student", newStudent);
        return "student";
    }

    @RequestMapping(value ="/addStudent.html", method = RequestMethod.POST)
    public String addStudent(@ModelAttribute("student") Student student){
        System.out.println(student.getFirstname()+" "+ student.getLastname()+
                " "+ student.getEmail()+" "+ student.getTelephone()+" ");
        return "redirect:student";
    }
}
