import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'mkbiza-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  
  imageSourceLogo = '';
}
