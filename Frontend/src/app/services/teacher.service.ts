import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Teacher } from '../teachers/teacher';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  private http = inject(HttpClient);
  private teachersUrl = 'http://localhost:8091/teachers';

  getTeachers(): Observable<Teacher[]> {
    return this.http.get<Teacher[]>(this.teachersUrl)
      .pipe(
        tap(_ => console.log('Fetched teachers')),
        catchError(this.handleError<Teacher[]>('getTeachers', []))
      );
  }

  getTeacher(id: number): Observable<Teacher> {
    return this.http.get<Teacher>(`${this.teachersUrl}/${id}`)
      .pipe(
        tap(_ => console.log(`Fetched teacher id=${id}`)),
        catchError(this.handleError<Teacher>(`getTeacher id=${id}`))
      );
  }

  addTeacher(teacher: Teacher): Observable<Teacher> {
    return this.http.post<Teacher>(this.teachersUrl, teacher, httpOptions)
      .pipe(
        tap((newTeacher: Teacher) => console.log(`Added teacher w/ id=${newTeacher.id}`)),
        catchError(this.handleError<Teacher>('addTeacher'))
      );
  }

  updateTeacher(teacher: Teacher, id: number): Observable<Teacher> {
    return this.http.put<Teacher>(`${this.teachersUrl}/${id}`, teacher, httpOptions)
      .pipe(
        tap(_ => console.log(`Updated teacher id=${id}`)),
        catchError(this.handleError<any>('updateTeacher'))
      );
  }

  deleteTeacher(id: number): Observable<Teacher> {
    return this.http.delete<Teacher>(`${this.teachersUrl}/${id}`, httpOptions)
      .pipe(
        tap(_ => console.log(`Deleted teacher id=${id}`)),
        catchError(this.handleError<Teacher>('deleteTeacher'))
      );
  }

  patchTeacher(id: number, data: Partial<Teacher>): Observable<Teacher> {
    return this.http.patch<Teacher>(`${this.teachersUrl}/${id}`, data, httpOptions)
      .pipe(
        tap(_ => console.log(`Partially updated teacher id=${id}`)),
        catchError(this.handleError<Teacher>('patchTeacher'))
      );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }
}
