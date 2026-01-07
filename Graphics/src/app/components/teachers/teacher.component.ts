import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule, NgForOf} from '@angular/common';
import { NgxPaginationModule } from 'ngx-pagination';
import {Teacher} from './teacher';
import {TeacherService} from '../services/teacher.service';
import {TeacherDTO} from './teacher.dto';

@Component({
  selector: 'app-teacher',
  templateUrl: './teacher.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgForOf,
    NgxPaginationModule
  ],
  styleUrls: ['./teacher.component.css']
})
export class TeacherComponent implements OnInit {
  teachers: Teacher[] = [];
  firstname: string = '';
  lastname: string = '';
  email: string = '';
  number: string = '';
  searchLastName: string = '';
  selectedTeacherId: number | null = null;
  p: number = 1;

  @Input() isTeacher: boolean = false;

  teacherForm!: FormGroup;
  isEditing = false;
  editingTeacherId: number | null = null;
  constructor(private teacherService: TeacherService,
              private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.teacherForm = new FormGroup({
      firstname: new FormControl('', Validators.required),
      lastname: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      number: new FormControl('', Validators.required)
    });

    this.loadTeachers();
  }

  loadTeachers() {
    this.teacherService.getTeachers().subscribe(teachers => {
      this.teachers = teachers;
    });
  }

  Search() {
    if (this.searchLastName.trim() === '') {
      this.loadTeachers();
    } else {
      this.teachers = this.teachers.filter(teacher =>
        teacher.lastname?.toLowerCase().includes(this.searchLastName.toLowerCase())
      );
    }
  }

  AddTeacher() {
    const teacher: Teacher = {
      firstname: this.firstname,
      lastname: this.lastname,
      email: this.email,
      number: this.number,
      teacherCourses: []
    };

    this.teacherService.addTeacher(teacher).subscribe(newTeacher => {
      this.teachers.push(newTeacher);
      this.clearForm();
      alert('Teacher successfully added!');
    });
  }

  deleteTeacher(id: number | undefined) {
    if (!id) return;

    if (confirm('Are you sure you want to delete this teacher?')) {
      this.teacherService.deleteTeacher(id).subscribe({
        next: () => {
          this.teachers = this.teachers.filter(teacher => teacher.id !== id);
          alert('Teacher deleted successfully!');
        },
        error: (err) => {
          console.error('Error deleting teacher:', err);
          alert('Failed to delete teacher.');
        }
      });
    }
  }

  editTeacher(teacher: Teacher) {
    this.isEditing = true;
    this.editingTeacherId = teacher.id!;
    this.teacherForm.patchValue({
      firstname: teacher.firstname,
      lastname: teacher.lastname,
      email: teacher.email,
      number: teacher.number
    });
  }

  updateTeacher() {
    if (this.teacherForm.invalid || this.editingTeacherId === null) {
      alert('Please fill all fields correctly');
      return;
    }

    const updatedTeacher: Teacher = this.teacherForm.value;

    this.teacherService.updateTeacher(updatedTeacher, this.editingTeacherId).subscribe({
      next: () => {
        alert('Student updated successfully!');
        this.loadTeachers();
        this.isEditing = false;
        this.editingTeacherId = null;
        this.teacherForm.reset();
      },
      error: (err) => {
        console.error('Error updating teacher:', err);
        alert('Failed to update teacher.');
      }
    });
  }

  cancelEdit() {
    this.isEditing = false;
    this.editingTeacherId = null;
    this.teacherForm.reset();
  }

  clearForm() {
    this.firstname = '';
    this.lastname = '';
    this.email = '';
    this.number = '';
    this.selectedTeacherId = null;
  }
}
