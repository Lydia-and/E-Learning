import {Student} from '../students/student';
import {Subject} from '../subjects/subject';

export interface Enrollment {
  id?: number;
  student: Student;
  subject: Subject;
  grade?: number | null;
}
