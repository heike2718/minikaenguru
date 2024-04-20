import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Rohpunktitem } from '@mkbiza-app/domain-model';

@Component({
  selector: 'mkbiza-rohpunktitem',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rohpunktitem.component.html',
  styleUrl: './rohpunktitem.component.scss',
})
export class RohpunktitemComponent {

  @Input()
  rohpunktitem!: Rohpunktitem;

}
