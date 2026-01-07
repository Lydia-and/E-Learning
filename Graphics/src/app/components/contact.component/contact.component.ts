import { Component } from '@angular/core';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent {
  // Cette structure doit correspondre exactement aux ngModel du HTML
  formData = {
    name: '',
    email: '',
    message: ''
  };

  onSubmit() {
    if (this.formData.name && this.formData.email) {
      console.log('Données du formulaire :', this.formData);
      alert('Message envoyé avec succès !');
    }
  }
}