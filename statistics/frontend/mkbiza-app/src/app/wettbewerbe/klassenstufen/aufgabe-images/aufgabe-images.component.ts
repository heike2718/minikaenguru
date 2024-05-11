import { Component, Input } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { CdkAccordionModule } from '@angular/cdk/accordion';
import { MatExpansionModule } from '@angular/material/expansion';
import { Images } from '@mkbiza-app/domain-model';

@Component({
  selector: 'mkbiza-aufgabe-images',
  standalone: true,
  imports: [
    CommonModule,
    CdkAccordionModule,
    MatExpansionModule,
    NgIf
  ],
  templateUrl: './aufgabe-images.component.html',
  styleUrl: './aufgabe-images.component.scss',
})
export class AufgabeImagesComponent {

  @Input()
  images!: Images;
}
