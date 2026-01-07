import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from "./components/home-page.component/home-page.component";
import { AboutComponent } from "./components/about.component/about.component";
import { ContactComponent } from "./components/contact.component/contact.component";

export const routes: Routes = [
  { path: 'app-home-page', component: HomePageComponent },
  { path: 'app-about', component: AboutComponent },
  { path: 'app-contact', component: ContactComponent },
  { path: '', redirectTo: '/app-home-page', pathMatch: 'full' } // Page par défaut
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }