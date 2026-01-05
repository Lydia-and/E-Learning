import {Component, Input, OnInit} from '@angular/core';
import {StudentService} from '../services/student.service';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule, NgForOf} from '@angular/common';
import {StudentDTO} from './student.dto';
import {NgxPaginationModule} from 'ngx-pagination';

@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NgForOf,
    ReactiveFormsModule,
    NgxPaginationModule
  ],
  styleUrls: ['./student.component.css']
})
export class StudentComponent implements OnInit {
  students: StudentDTO[] = [];
  lastName: any;
  p: number = 1;

  newStudent = {
    firstname: '',
    lastname: '',
    email: '',
    telephone: ''
  };

  @Input() isTeacher: boolean = false;

  studentForm!: FormGroup;
  isEditing = false;
  editingStudentId: number | null = null;

  constructor(private studentService: StudentService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.loadStudents();

    this.studentForm = this.formBuilder.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required/*, Validators.email*/]],
      telephone: ['', Validators.required]
    });
  }

  addStudent() {
    if (!this.newStudent.firstname || !this.newStudent.lastname || !this.newStudent.email || !this.newStudent.telephone) {
      alert('Please fill in all required fields.');
      return;
    }

    const studentDTO: StudentDTO = {
      firstname: this.newStudent.firstname,
      lastname: this.newStudent.lastname,
      email: this.newStudent.email,
      telephone: this.newStudent.telephone
    };

    this.studentService.addStudent(studentDTO).subscribe({
      next: (createdStudent) => {
        this.students.push(createdStudent);
        alert('Student added successfully!');
        //to clean up
        this.newStudent = { firstname: '', lastname: '', email: '', telephone: '' };
      },
      error: (err) => {
        console.error('Error adding student:', err);
        alert('Failed to add student.');
      }
    });
  }

  deleteStudent(id: number | undefined) {
    if (!id) return;

    if (confirm('Are you sure you want to delete this student?')) {
      this.studentService.deleteStudent(id).subscribe({
        next: () => {
          this.students = this.students.filter(student => student.id !== id);
          alert('Student deleted successfully!');
        },
        error: (err) => {
          console.error('Error deleting student:', err);
          alert('Failed to delete student.');
        }
      });
    }
  }

  editStudent(student: StudentDTO) {
    this.isEditing = true;
    this.editingStudentId = student.id!;
    this.studentForm.patchValue({
      firstname: student.firstname,
      lastname: student.lastname,
      email: student.email,
      telephone: student.telephone
    });
  }

  updateStudent() {
    if (this.studentForm.invalid || this.editingStudentId === null) {
      alert('Please fill all fields correctly');
      return;
    }

    const updatedStudent: StudentDTO = this.studentForm.value;

    this.studentService.updateStudent(updatedStudent, this.editingStudentId ).subscribe({
      next: () => {
        alert('Student updated successfully!');
        this.loadStudents();
        this.isEditing = false;
        this.editingStudentId = null;
        this.studentForm.reset();
      },
      error: (err) => {
        console.error('Error updating student:', err);
        alert('Failed to update student.');
      }
    });
  }

  cancelEdit() {
    this.isEditing = false;
    this.editingStudentId = null;
    this.studentForm.reset();
  }

  loadStudents() {
    this.studentService.getStudents().subscribe(data => {
      this.students = data;
    });
  }

  Search() {
    if(this.lastName.trim() == '') {
      this.ngOnInit()
    } else {
      this.students = this.students.filter(stud => {
        return stud.lastname.toLocaleLowerCase().match(this.lastName.toLocaleLowerCase())
      })
    }
  }
}
