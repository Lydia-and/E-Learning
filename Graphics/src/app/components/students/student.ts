import {Enrollment} from '../enrollments/enrollment';

export class Student {

  id?: number;
  firstname: string;
  lastname: string;
  email: string;
  telephone: string;
  enrollments?: Enrollment[];

  constructor(firstname: string, lastname: string, email: string, telephone: string, enrollments: Enrollment[]) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.telephone = telephone;
    this.enrollments = enrollments;
  }
}
