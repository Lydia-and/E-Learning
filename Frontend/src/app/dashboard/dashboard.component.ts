import {Component, Input, OnInit} from '@angular/core';
import { TokenStorageService } from '../auth/token-storage.service';
import {Router, RouterModule} from '@angular/router';
import {CommonModule, NgClass} from '@angular/common';
import { FormsModule } from '@angular/forms';
import {UserService} from '../services/user.service';
import {SubjectService} from '../services/subject.service';
import {TeacherService} from '../services/teacher.service';
import {EnrollmentService} from '../services/enrollment.service';
import {DashboardService} from '../services/dashboard.service';
import {StudentService} from '../services/student.service';
import {StudentComponent} from '../students/student.component';
import {SubjectComponent} from '../subjects/subject.component';
import {EnrollmentComponent} from '../enrollments/enrollment.component';
import {TeacherComponent} from '../teachers/teacher.component';
import {EnrollmentDTO} from '../enrollments/enrollment.dto';
import {SubjectDTO} from '../subjects/subject.dto';

interface Notification {
  type: string;
  message: string;
  time: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    FormsModule,
    StudentComponent,
    SubjectComponent,
    SubjectComponent,
    EnrollmentComponent,
    TeacherComponent
  ],
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  user: any = {};
  username: string = '';
  responseMessage: any;
  data:any;

  activeSection: 'dashboard' | 'subjects' | 'students' | 'teachers' | 'enroll' | 'enrollments' = 'dashboard';

  subjects: SubjectDTO[] = [];
  enrollments: (EnrollmentDTO & {
    studentFirstName?: string;
    studentLastName?: string;
    subjectName?: string;
  })[] = [];

  @Input() isTeacher: boolean = false;
  isStudent = false;

  constructor(
    private dashboardService: DashboardService,
    private token: TokenStorageService,
    private router: Router,
    private subjectService: SubjectService,
    private enrollmentService: EnrollmentService,
    private studentService: StudentService,
    private teacherService: TeacherService,
    private userService: UserService
  ) {}


  ngOnInit() {
    this.user.username = this.token.getUsername();
    //this is for enroll
    this.username = this.user.username;

    //to extract the authority value before include as it is before an object and not a string
    const roles = this.token.getAuthorities().map((role: any) => role.authority);
    console.log('Roles:', roles);
    this.isTeacher = roles.includes('ROLE_ADMIN');
    this.isStudent = roles.includes('ROLE_USER');
    this.activeSection = 'dashboard';
  }



  enroll(subjectId: number) {
    if (!subjectId) {
      alert('Invalid subject ID');
      return;
    }
    const enrollment = {
      subject: { id: subjectId },
      student: { username: this.username }
    };
    this.enrollmentService.addEnrollment(enrollment as any).subscribe(() => {
      alert('Enrolled successfully!');
    });
  }

  // to show the correct version depending on the authority
  showSection(section: typeof this.activeSection) {
    this.activeSection = section;

    if (section === 'enrollments') {
      this.enrollmentService.getEnrollments().subscribe(data => this.enrollments = data);
    }
    if (section === 'subjects') {
      this.subjectService.getSubjects().subscribe(data => this.subjects = data);
    }
  }


  logout() {
    this.token.signOut();
    this.router.navigate(['/login']);
  }

}
