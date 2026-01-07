import {Teacher} from '../teachers/teacher';
import {Subject} from '../subjects/subject';

export interface TeacherCourse {
  id: number;
  teacher: Teacher;
  subject: Subject;
  role: string;
}
