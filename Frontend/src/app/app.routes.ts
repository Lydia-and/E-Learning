import { Routes } from '@angular/router';
import {SignupPageComponent} from './signup-page/signup-page.component';
import {LoginComponent} from './login/login.component';
import {HomePageComponent} from './Home-page/home-page.component';
import {DashboardComponent} from './dashboard/dashboard.component';

export const routes: Routes = [

  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupPageComponent },
  { path:'dashboard', component: DashboardComponent },
  { path: '', component: HomePageComponent }
];
