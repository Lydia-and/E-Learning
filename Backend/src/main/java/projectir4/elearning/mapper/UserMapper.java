package projectir4.elearning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import projectir4.elearning.dto.StudentDTO;
import projectir4.elearning.dto.TeacherDTO;
import projectir4.elearning.dto.UserDTO;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.User;
import projectir4.elearning.model.Student;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserSummary(User user);
    TeacherDTO toTeacherDTO(Teacher teacher);
    StudentDTO toStudentDTO(Student student);
    //User toEntity(RegisterUserRequest request);
    //void update(UpdateUserRequest request, @MappingTarget User user);
}