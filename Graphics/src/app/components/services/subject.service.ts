import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {Subject} from '../subjects/subject';
import {SubjectDTO} from '../subjects/subject.dto';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

  private http = inject(HttpClient);
  private subjectsUrl = 'http://localhost:8091/subjects';

  /** GET all subjects */
  getSubjects(): Observable<SubjectDTO[]> {
    return this.http.get<SubjectDTO[]>(this.subjectsUrl).pipe(
      tap(_ => console.log('fetched subjects')),
      catchError(this.handleError<SubjectDTO[]>('getSubjects', []))
    );
  }

  /** GET subject by id */
  getSubject(id: number): Observable<Subject> {
    const url = `${this.subjectsUrl}/${id}`;
    return this.http.get<Subject>(url).pipe(
      tap(_ => console.log(`fetched subject id=${id}`)),
      catchError(this.handleError<Subject>(`getSubject id=${id}`))
    );
  }

  /** POST add a new subject (prof) */
  addSubject(subject: SubjectDTO): Observable<SubjectDTO> {
    return this.http.post<SubjectDTO>(this.subjectsUrl, subject, httpOptions).pipe(
      tap((newSubject: SubjectDTO) => console.log(`added subject id=${newSubject.id}`)),
      catchError(this.handleError<SubjectDTO>('addSubject'))
    );
  }


  updateSubject(subject: SubjectDTO, id: number): Observable<Subject> {
    const url = `${this.subjectsUrl}/${id}`;
    return this.http.put<Subject>(url, subject, httpOptions).pipe(
      tap(_ => console.log(`updated subject id=${id}`)),
      catchError(this.handleError<Subject>('updateSubject'))
    );
  }


  patchSubject(id: number, updateData: Partial<Subject>): Observable<Subject> {
    const url = `${this.subjectsUrl}/${id}`;
    return this.http.patch<Subject>(url, updateData, httpOptions).pipe(
      tap(_ => console.log(`partially updated subject id=${id}`)),
      catchError(this.handleError<Subject>('patchSubject'))
    );
  }

  deleteSubject(id: number): Observable<void> {
    const url = `${this.subjectsUrl}/${id}`;
    return this.http.delete<void>(url, httpOptions)
      .pipe(
        tap(_ => console.log(`deleted subject id=${id}`)),
        catchError(this.handleError<void>('deleteSubject'))
      );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }
}
