import { Subject } from '../subjects/subject';
import {TeacherCourse} from '../teacher-Course/teacher-Course';

export interface Teacher {
  id?: number;
  firstname: string;
  lastname: string;
  email: string;
  number: string;
  teacherCourses?: TeacherCourse[];
}
