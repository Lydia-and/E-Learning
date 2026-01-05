import {EnrollmentDTO} from '../enrollments/enrollment.dto';

export interface StudentDTO {
  id?: number;
  firstname: string;
  lastname: string;
  email: string;
  telephone: string;
  enrollments?: EnrollmentDTO[];
}
