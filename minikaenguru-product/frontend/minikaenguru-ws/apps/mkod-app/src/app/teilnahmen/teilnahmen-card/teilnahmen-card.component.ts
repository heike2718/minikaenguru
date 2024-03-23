import { Component, OnInit, Input } from '@angular/core';
import { Anmeldungsitem } from '../../shared/beteiligungen.model';

@Component({
  selector: 'mkod-teilnahmen-card',
  templateUrl: './teilnahmen-card.component.html',
  styleUrls: ['./teilnahmen-card.component.css']
})
export class TeilnahmenCardComponent implements OnInit {


  @Input()
	anmeldungsitem!: Anmeldungsitem;

  constructor() { }

  ngOnInit(): void { }

  isOverview(): boolean {

    return this.anmeldungsitem.name === 'Privatanmeldungen' || this.anmeldungsitem.name === 'Schulanmeldungen';
  }

}
