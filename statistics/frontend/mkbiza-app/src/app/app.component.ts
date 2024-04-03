import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeComponent } from 'home/home.component';

@Component({
  standalone: true,
  imports: [
    HomeComponent,
    RouterModule
  ],
  selector: 'mkbiza-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'Minikänguru-Statistiken';
}
