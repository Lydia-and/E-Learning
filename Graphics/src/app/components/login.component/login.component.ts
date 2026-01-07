import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { TokenStorageService } from '../auth/token-storage.service';
import {LoginInfo} from '../auth/login-info';
import {JwtResponse} from '../auth/jwt-response';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl : './login.component.css',
})
export class LoginComponent implements OnInit {
  loginData = {
    username: '',
    password: ''
  };

  isLoggedIn = false;
  isLoginFailed = false;
  private tokenStorage = inject(TokenStorageService);

  constructor(private AuthService: AuthService, private router: Router) {}

  ngOnInit() {
    if (this.tokenStorage.getToken() !== '{}') {
      this.isLoggedIn = true;
      this.router.navigate(['/dashboard']);
    }
  }

  onLoginSubmit() {
    const loginInfo = new LoginInfo(this.loginData.username, this.loginData.password);

    this.AuthService.attemptAuth(loginInfo).subscribe({
      next: (response: JwtResponse) => {
        this.tokenStorage.saveToken(response.accessToken || '{}');
        this.tokenStorage.saveUsername(response.username || '{}');
        this.tokenStorage.saveAuthorities(response.authorities || []);

        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.router.navigate(['/dashboard']);
      },
      error: error => {
        console.error('Login failed:', error);
        this.isLoginFailed = true;
        alert('Login failed. Please check your credentials.');
      }
    });
  }

  goToSignup() {
    this.router.navigate(['/signup']);
  }
}
