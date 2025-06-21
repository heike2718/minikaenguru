import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkAccordionModule } from '@angular/cdk/accordion';
import { MatExpansionModule } from '@angular/material/expansion';
import { Images } from '@mks/domain-model';

@Component({
    selector: 'mks-aufgabe-images',
    imports: [
        CommonModule,
        CdkAccordionModule,
        MatExpansionModule
    ],
    templateUrl: './aufgabe-images.component.html',
    styleUrl: './aufgabe-images.component.scss'
})
export class AufgabeImagesComponent {

  @Input()
  images!: Images;
}
