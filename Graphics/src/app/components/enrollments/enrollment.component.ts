import {Component, Input, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgxPaginationModule} from 'ngx-pagination';
import {CommonModule, NgForOf} from '@angular/common';
import {EnrollmentService} from '../services/enrollment.service';
import {SubjectService} from '../services/subject.service';
import {Subject} from '../subjects/subject';
import {StudentService} from '../services/student.service';
import {EnrollmentDTO} from './enrollment.dto';
import {StudentDTO} from '../students/student.dto';
import {TokenStorageService} from '../auth/token-storage.service';

@Component({
  selector: 'app-enrollment',
  templateUrl: './enrollment.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NgxPaginationModule,
    ReactiveFormsModule,
    NgForOf
  ],
  styleUrls: ['./enrollment.component.css']
})
export class EnrollmentComponent implements OnInit {
  enrollments: (EnrollmentDTO & {
    studentFirstName?: string;
    studentLastName?: string;
    subjectName?: string;
  })[] = [];
  ID: string = '';
  p: number = 1;

  @Input() isTeacher: boolean = false;
  @Input() isStudent: boolean = false;

  studentId!: number;
  subjectId!: number;
  foundStudent: StudentDTO | null = null;
  foundSubject: Subject | null = null;
  grade: number | null = null;

  constructor(private enrollmentService: EnrollmentService,
              private subjectService: SubjectService,
              private studentService: StudentService,
              private token: TokenStorageService) { }

  ngOnInit(): void {
    this.loadEnrollments();

    if (this.isStudent) {
      const username = this.token.getUsername();

      this.studentService.getStudents().subscribe(students => {
        const found = students.find(st => st.firstname === username);

        if (found) {
          this.foundStudent = found;
          this.studentId = found.id!;
        } else {
          alert("No matching student found for this user");
        }
      });
    }

  }

  loadEnrollments() {
    //reset to avoid double
    this.enrollments = [];
    this.enrollmentService.getEnrollments().subscribe(enrollments => {
      enrollments.forEach(en => {
        this.studentService.getStudent(en.studentId).subscribe(student => {
          this.subjectService.getSubject(en.subjectId).subscribe(subject => {
            this.enrollments.push({
              ...en,
              studentFirstName: student.firstname,
              studentLastName: student.lastname,
              subjectName: subject.name
            });
          });
        });
      });
    });
  }

  SearchEnrollment() {
    if (this.ID.trim() === '') {
      this.loadEnrollments();
    } else {
      this.enrollments = this.enrollments.filter(en =>
        en.id!.toString().includes(this.ID)
      );
    }
  }

  SearchSubject(){
    this.subjectService.getSubject(this.subjectId).subscribe(subject=>{
      this.foundSubject = subject;
      if(!subject){
        alert("This subject does not exist yet");
      }
    })
  }

  SearchStudent() {
    this.studentService.getStudent(this.studentId).subscribe(student => {
      this.foundStudent = student;
      if (!student)
        alert("This student does not exist");
    });
  }

  Enroll() {

    const username = localStorage.getItem('username'); // ou via ton TokenStorageService
    if (!username) {
      alert("Utilisateur non authentifié.");
      return;
    }

    this.studentService.getStudents().subscribe({
      next: students => {
        const student = students.find(s => s.firstname === username);
        if (!student) {
          alert("Étudiant introuvable.");
          return;
        }

        // Créer l'enrollment
        const enrollment: EnrollmentDTO = {
          studentId: student.id!,
          subjectId: this.subjectId,
          grade: this.grade
        };

        this.enrollmentService.addEnrollment(enrollment).subscribe({
          next: newEnrollment => {
            this.enrollments.push({
              ...newEnrollment,
              studentFirstName: student.firstname,
              studentLastName: student.lastname,
              subjectName: this.foundSubject?.name
            });
            alert("Inscription réussie !");
          },
          error: err => {
            console.error("Erreur lors de l'inscription :", err);
            alert("Erreur pendant l'inscription.");
          }
        });
      },
      error: err => {
        console.error("Erreur lors de la récupération des étudiants :", err);
      }
    });
  }
}
