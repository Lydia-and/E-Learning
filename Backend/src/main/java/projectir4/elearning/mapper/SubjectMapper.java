package projectir4.elearning.mapper;

import org.mapstruct.Mapper;
import projectir4.elearning.dto.SubjectDTO;
import projectir4.elearning.model.Subject;
import projectir4.elearning.model.Teacher;
import projectir4.elearning.model.TeacherCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    SubjectDTO toDTO(Subject subject);

    default Subject toEntity(SubjectDTO dto) {
        if (dto == null) return null;

        Subject subject = new Subject();
        subject.setId(dto.getId());
        subject.setName(dto.getName());
        subject.setCoefficient(dto.getCoefficient());
        return subject;
    }
}
