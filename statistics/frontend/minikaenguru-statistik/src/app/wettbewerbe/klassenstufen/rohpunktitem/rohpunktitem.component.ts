import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Rohpunktitem } from '@mks/domain-model';

@Component({
    selector: 'mks-rohpunktitem',
    imports: [CommonModule],
    templateUrl: './rohpunktitem.component.html',
    styleUrl: './rohpunktitem.component.scss'
})
export class RohpunktitemComponent {

  @Input()
  rohpunktitem!: Rohpunktitem;

}
