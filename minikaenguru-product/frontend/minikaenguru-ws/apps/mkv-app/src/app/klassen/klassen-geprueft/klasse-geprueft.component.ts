import { Component, Input, OnInit } from '@angular/core';
import { KlassenFacade } from '../klassen.facade';

@Component({
  selector: 'mkv-klasse-geprueft',
  templateUrl: './klasse-geprueft.component.html',
  styleUrls: ['./klasse-geprueft.component.css']
})
export class KlasseGeprueftComponent implements OnInit {

  @Input()
  klasseID!: string;

  constructor(private klassenFacade: KlassenFacade) { }

  ngOnInit() {
  }

  onCheckboxKlasseKorrigiertClicked(event: boolean) {
		if (event) {
			this.klassenFacade.markKlasseKorrigiert(this.klasseID);
		}		
	}
}
