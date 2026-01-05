import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgxPaginationModule} from 'ngx-pagination';
import {SubjectService} from '../services/subject.service';
import {CommonModule, NgForOf} from '@angular/common';
import {SubjectDTO} from './subject.dto';

@Component({
  selector: 'app-subject',
  templateUrl: './subject.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NgxPaginationModule,
    ReactiveFormsModule,
    NgForOf
  ],
  styleUrls: ['./subject.component.css']
})
export class SubjectComponent implements OnInit {
  subjects: SubjectDTO[] = [];
  Name: any;
  search: any;
  coefficient!: number;
  p: number = 1;

  newSubject = {
    name: '',
    coefficient: ''
  };

  @Input() isTeacher: boolean = false;

  classForm!: FormGroup;
  isEditing = false;
  editingClassId: number | null = null;

  constructor(private subjectService: SubjectService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.loadSubjects();

    this.classForm = this.formBuilder.group({
      name: ['', Validators.required],
      coefficient: ['', Validators.required]
    })
  }

  Search() {
    if(this.Name == '') {
      this.ngOnInit()
    } else {
      this.subjects = this.subjects.filter(sub => {
        return sub.name.toLocaleLowerCase().match(this.search.toLocaleLowerCase())
      })
    }
  }

  AddClass(){
    if (!this.newSubject.name || !this.newSubject.coefficient) {
      alert('Please fill in all required fields.');
      return;
    }

    const subject: SubjectDTO = {
      name: this.Name,
      coefficient: this.coefficient
    };

    this.subjectService.addSubject(subject).subscribe({
      next: (createdSubject: SubjectDTO) => {
        this.subjects.push(createdSubject);
        alert("Class succesfully added");
        //just to clean up the form after its use
        this.Name = '';
        this.coefficient = 0;
      },
      error: err => {
        console.error('Error adding class:', err);
      }
    });
  }

  deleteClass(id: number | undefined){
    if(!id) return;

    if(confirm('Are you sure you want to delete this class?')){
      this.subjectService.deleteSubject(id).subscribe({
        next: ()=>{
          this.subjects=this.subjects.filter(subject=>subject.id==id);
          alert('Class deleted successfully!');
        },
        error: (err) => {
          console.error('Error deleting class:', err);
          alert('Failed to delete class.');
        }
      })
    }
  }

  editClass(subject: SubjectDTO){
    this.isEditing=true;
    this.editingClassId=subject.id!;
    this.classForm.patchValue({
      name: subject.name,
      coefficient: subject.coefficient
    })
  }

  updateClass(){
    if (this.classForm.invalid || this.editingClassId === null) {
      alert('Please fill all fields correctly');
      return;
    }

    const updatedSubject: SubjectDTO = this.classForm.value;

    this.subjectService.updateSubject(updatedSubject, this.editingClassId).subscribe({
      next: ()=>{
        alert('Class edited successfully');
        this.loadSubjects();
        this.isEditing=false;
        this.editingClassId=null;
        this.classForm.reset()
      },
      error: (err) => {
        console.error('Error updating class:', err);
        alert('Failed to update class.');
      }
    })
  }

  cancelEdit(){
    this.isEditing=false;
    this.editingClassId=null;
    this.classForm.reset;
  }

  loadSubjects() {
    this.subjectService.getSubjects().subscribe((sub) => {
      this.subjects = sub;
    })
  }
}
