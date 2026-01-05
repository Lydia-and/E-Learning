import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {LoginInfo} from './login-info';
import {Observable} from 'rxjs';
import {JwtResponse} from './jwt-response';
import {SignupInfo} from './signup-info';
import {Router} from '@angular/router';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private loginUrl = 'http://localhost:8090/auth/signin';
  private signupUrl = 'http://localhost:8090/auth/signup';

  constructor(private http: HttpClient,
              private router: Router) { }

  attemptAuth(credentials: LoginInfo): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(this.loginUrl, credentials, httpOptions);
  }

  signUp(info: SignupInfo): Observable<string> {
    const body = {
      username: info.username,
      password: info.password,
      role: info.role,
      secretCode: info.secretCode
    };
    return this.http.post<string>(this.signupUrl, info, httpOptions);
  }

  attemptSignup(signupInfo: SignupInfo) {
    return this.http.post('/api/auth/signup', signupInfo);
  }

  public isAuthenticated():boolean{
    const token = localStorage.getItem("token");
    if(!token){
      this.router.navigate(['/']);
      return false;
    }else{
      return true;
    }
  }
}

