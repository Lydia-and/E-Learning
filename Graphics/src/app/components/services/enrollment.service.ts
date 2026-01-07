import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {Enrollment} from '../enrollments/enrollment';
import {EnrollmentDTO} from '../enrollments/enrollment.dto';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {

  private http = inject(HttpClient);
  private enrollmentsUrl = 'http://localhost:8091/enrollments';

  getEnrollments(): Observable<EnrollmentDTO[]> {
    return this.http.get<EnrollmentDTO[]>(this.enrollmentsUrl).pipe(
      tap(_ => this.log('fetched enrollments')),
      catchError(this.handleError<EnrollmentDTO[]>('getEnrollments', []))
    );
  }

  getEnrollment(id: number): Observable<EnrollmentDTO> {
    const url = `${this.enrollmentsUrl}/${id}`;
    return this.http.get<EnrollmentDTO>(url).pipe(
      tap(_ => this.log(`fetched enrollment id=${id}`)),
      catchError(this.handleError<EnrollmentDTO>(`getEnrollment id=${id}`))
    );
  }

  addEnrollment(enrollment: EnrollmentDTO): Observable<EnrollmentDTO> {
    return this.http.post<EnrollmentDTO>(this.enrollmentsUrl, enrollment, httpOptions).pipe(
      tap((newEnrollment: EnrollmentDTO) => this.log(`added enrollment id=${newEnrollment.id}`)),
      catchError(this.handleError<EnrollmentDTO>('addEnrollment'))
    );
  }

  updateEnrollment(enrollment: EnrollmentDTO, id: number): Observable<EnrollmentDTO> {
    return this.http.put<EnrollmentDTO>(`${this.enrollmentsUrl}/${id}`, enrollment, httpOptions).pipe(
      tap(_ => this.log(`updated enrollment id=${id}`)),
      catchError(this.handleError<any>('updateEnrollment'))
    );
  }

  patchEnrollment(id: number, updateData: Partial<EnrollmentDTO>): Observable<EnrollmentDTO> {
    return this.http.patch<EnrollmentDTO>(`${this.enrollmentsUrl}/${id}`, updateData, httpOptions).pipe(
      tap(_ => this.log(`patched enrollment id=${id}`)),
      catchError(this.handleError<EnrollmentDTO>('patchEnrollment'))
    );
  }

  deleteEnrollment(id: number): Observable<EnrollmentDTO> {
    return this.http.delete<EnrollmentDTO>(`${this.enrollmentsUrl}/${id}`, httpOptions).pipe(
      tap(_ => this.log(`deleted enrollment id=${id}`)),
      catchError(this.handleError<EnrollmentDTO>('deleteEnrollment'))
    );
  }

  deleteAllEnrollments(): Observable<EnrollmentDTO> {
    return this.http.delete<EnrollmentDTO>(this.enrollmentsUrl, httpOptions).pipe(
      tap(_ => this.log('deleted all enrollments')),
      catchError(this.handleError<EnrollmentDTO>('deleteAllEnrollments'))
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
    console.log('EnrollmentService: ' + message);
  }
}
