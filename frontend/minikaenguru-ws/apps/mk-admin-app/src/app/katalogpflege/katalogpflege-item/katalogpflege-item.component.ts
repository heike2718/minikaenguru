import { Component, OnInit, Input } from '@angular/core';
import { KatalogpflegeItem } from '../katalogpflege.model';
import { KatalogpflegeFacade } from '../katalogpflege.facade';

@Component({
	selector: 'mka-katalogpflege-item',
	templateUrl: './katalogpflege-item.component.html',
	styleUrls: ['./katalogpflege-item.component.css']
})
export class KatalogpflegeItemComponent implements OnInit {


	@Input()
	katalogItem: KatalogpflegeItem;

	anzahlText: string;

	sucheBtnText: string

	constructor(private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {

		switch (this.katalogItem.typ) {
			case 'LAND': this.anzahlText = 'Anzahl Orte:'; this.sucheBtnText = 'Orte'; break;
			case 'ORT': this.anzahlText = 'Anzahl Schulen:'; this.sucheBtnText = 'Schulen'; break;
			default: this.anzahlText = ''; this.sucheBtnText = '';
		}
	}

	editItem() {
		this.katalogFacade.gotoEditor(this.katalogItem);
	}

	openOrSearchChilds() {
		this.katalogFacade.gotoChildItems(this.katalogItem);
	}

}
