import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HomePageComponent } from "./components/home-page.component/home-page.component";
import { AboutComponent } from "./components/about.component/about.component";
import { ContactComponent } from "./components/contact.component/contact.component";
import { SignupPageComponent } from "./components/signup-page.component/signup-page.component";

@Component({
  selector: 'app-root',
  imports: [HomePageComponent, AboutComponent, ContactComponent, SignupPageComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})

export class App {
  protected readonly title = signal('Graphics');
}
