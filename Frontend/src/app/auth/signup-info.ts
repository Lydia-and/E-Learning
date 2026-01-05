export class SignupInfo {

  username: string;
  password: string;
  confirmPassword?: string;
  role?: string[];
  email: string;
  secretCode?: string;

  constructor(username: string, email:string, password: string,confirmPassword?: string, role ?: string[], secretCode?: string) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.confirmPassword = confirmPassword;
    this.role = role ;
    this.secretCode = secretCode;
  }
}

