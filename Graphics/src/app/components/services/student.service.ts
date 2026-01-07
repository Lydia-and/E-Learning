import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, catchError, Observable, of, tap} from 'rxjs';
import {Enrollment} from '../enrollments/enrollment';
import {Student} from '../students/student';
import {StudentDTO} from '../students/student.dto';
import {EnrollmentDTO} from '../enrollments/enrollment.dto';

const httpOptions = {
  headers: new HttpHeaders ({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private http = inject(HttpClient);

  private studentsUrl = 'http://localhost:8091/students';

  /** GET users from the server */
  getStudents(): Observable<StudentDTO[]> {
    return this.http.get<StudentDTO[]>(this.studentsUrl);
  }

  getStudent(id: number): Observable<StudentDTO> {
    return this.http.get<StudentDTO>(`${this.studentsUrl}/${id}`).pipe(
      tap(_ => this.log(`fetched student id=${id}`)),
      catchError(this.handleError<StudentDTO>(`getStudent id=${id}`))
    );
  }

  addStudent(student: StudentDTO): Observable<StudentDTO> {
    return this.http.post<StudentDTO>(this.studentsUrl, student, httpOptions).pipe(
      tap((studentAdded: StudentDTO) => this.log(`added student id=${studentAdded.id}`)),
      catchError(this.handleError<StudentDTO>('addStudent'))
    );
  }

  deleteStudent(student: StudentDTO | number): Observable<StudentDTO> {
    const id = typeof student === 'number' ? student : student.id;
    const url = `${this.studentsUrl}/${id}`;
    return this.http.delete<StudentDTO>(url, httpOptions).pipe(
      tap(_ => this.log(`deleted student id=${id}`)),
      catchError(this.handleError<StudentDTO>('deleteStudent'))
    );
  }

  deleteStudents(): Observable<StudentDTO> {
    return this.http.delete<StudentDTO>(this.studentsUrl, httpOptions).pipe(
      tap(_ => this.log(`deleted students`)),
      catchError(this.handleError<StudentDTO>('deleteStudents'))
    );
  }

  updateStudent(student: StudentDTO, id: number): Observable<StudentDTO> {
    return this.http.put<StudentDTO>(`${this.studentsUrl}/${id}`, student, httpOptions).pipe(
      tap((studentUpdated: StudentDTO) => this.log(`updated student id=${studentUpdated.id}`)),
      catchError(this.handleError<any>('updateStudent'))
    );
  }

  updateStudents(students: StudentDTO[]): Observable<StudentDTO[]> {
    return this.http.put<StudentDTO[]>(this.studentsUrl, students, httpOptions).pipe(
      tap(_ => this.log(`updated student list`)),
      catchError(this.handleError<any>('updateStudents'))
    );
  }

  patchStudent(id: number, updateData: Partial<StudentDTO>): Observable<StudentDTO> {
    const url = `${this.studentsUrl}/${id}`;
    return this.http.patch<StudentDTO>(url, updateData, httpOptions).pipe(
      tap((studentUpdated: StudentDTO) => this.log(`partially updated student id=${studentUpdated.id}`)),
      catchError(this.handleError<StudentDTO>('patchStudent'))
    );
  }

  getStudentsCounter(): Observable<number> {
    return this.http.get<number>(`${this.studentsUrl}/counter`);
  }

  public totalItems: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  getCartItems() {
    return this.totalItems.asObservable();
  }

  getEnrollmentsByStudentId(studentId: number): Observable<EnrollmentDTO[]> {
    return this.http.get<EnrollmentDTO[]>(`${this.studentsUrl}/${studentId}/enrollments`, httpOptions).pipe(
      tap(_ => this.log(`fetched enrollments for student id=${studentId}`)),
      catchError(this.handleError<EnrollmentDTO[]>('getEnrollmentsByStudentId', []))
    );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      this.log(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }

  private log(message: string) {
    console.log('StudentService: ' + message);
  }


}




