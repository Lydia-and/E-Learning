import {EnrollmentDTO} from '../enrollments/enrollment.dto';

export interface SubjectDTO {
  id?: number;
  name: string;
  coefficient: number;
  enrollments?: EnrollmentDTO[];
  teacherIds?: number[];
}
