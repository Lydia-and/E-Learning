import {Enrollment} from '../enrollments/enrollment';
import { Teacher } from '../teachers/teacher';

export interface Subject{
  id? : number;
  name: string;
  coefficient: number;
  teachers?: Teacher[];
  enrollments?: Enrollment[];

}
