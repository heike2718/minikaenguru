import { Component, Input, OnInit } from '@angular/core';
import { Median } from '../../shared/beteiligungen.model';

@Component({
  selector: 'mkod-median',
  templateUrl: './median.component.html',
  styleUrls: ['./median.component.css']
})
export class MedianComponent implements OnInit {

  @Input()
  median: Median;

  klassenstufe: string;

  constructor() { }

  ngOnInit(): void {

    this.klassenstufe = this.klassenstufeText();
  }

  private klassenstufeText(): string {

    switch(this.median.klassenstufe) {
      case 'IKID': return 'Integration';
      case 'EINS': return 'Klasse 1';
      case 'ZWEI': return 'Klasse 2';
    };
  }
}
