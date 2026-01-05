import { Component } from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { SignupInfo } from '../auth/signup-info';
import { AuthService } from '../auth/auth.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-signup-page',
  standalone: true,
  templateUrl: './signup-page.component.html',
  imports: [NgIf, FormsModule, RouterLink],
  styleUrls: ['./signup-page.component.css']
})
export class SignupPageComponent {
  signupData = {
    username: '',
    password: '',
    email: '',
    confirmPassword: '',
    role: 'user',
    secretCode: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  onSignupSubmit() {
    if (this.signupData.password !== this.signupData.confirmPassword) {
      alert('Passwords do not match.');
      return;
    }

    // Définir rôle en fonction de secretCode
    let role = ['USER'];
    if (this.signupData.secretCode && this.signupData.secretCode.trim().length > 0) {
      role = ['ADMIN'];
    }

    const signupInfo = new SignupInfo(
      this.signupData.username,
      this.signupData.email,
      this.signupData.password,
      this.signupData.confirmPassword,
      role,
      this.signupData.secretCode
    );

    this.authService.signUp(signupInfo).subscribe({
      next: () => {
        alert('Account created successfully! Please log in.');
        this.goToLogin();
      },
      error: (err: any) => {
        console.error('Signup failed', err);
        alert('Signup failed. Please try again.');
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  isAdminSelected(): boolean {
    return this.signupData.role === 'admin';
  }
}
