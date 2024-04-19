import { Component, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';

@Component({
  selector: 'mkbiza-error',
  standalone: true,
  imports: [CommonModule, NgIf, MatButtonModule],
  templateUrl: './error.component.html',
  styleUrl: './error.component.scss',
})
export class ErrorComponent {

  #router = inject(Router);

  gotoStart(): void {
    this.#router.navigateByUrl('startseite');
  }
}
