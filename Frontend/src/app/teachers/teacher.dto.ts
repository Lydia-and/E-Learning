import {TeacherCourseDTO} from '../teacher-Course/teacher-Course.dto';

export interface TeacherDTO {
  id?: number;
  firstname: string;
  lastname: string;
  email: string;
  number: string;
  teacherCourses?: TeacherCourseDTO[];
}
