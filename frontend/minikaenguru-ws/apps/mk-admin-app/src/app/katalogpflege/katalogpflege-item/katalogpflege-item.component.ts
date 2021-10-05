import { Component, OnInit, Input } from '@angular/core';
import { KatalogpflegeItem } from '../katalogpflege.model';
import { KatalogpflegeFacade } from '../katalogpflege.facade';
import { environment } from '../../../environments/environment';

@Component({
	selector: 'mka-katalogpflege-item',
	templateUrl: './katalogpflege-item.component.html',
	styleUrls: ['./katalogpflege-item.component.css']
})
export class KatalogpflegeItemComponent implements OnInit {


	devMode = environment.envName === 'DEV';

	@Input()
	katalogItem!: KatalogpflegeItem;

	anzahlText: string = '';

	sucheBtnText: string = '';

	constructor(private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {

		switch (this.katalogItem.typ) {
			case 'LAND': this.anzahlText = 'Anzahl Orte:'; this.sucheBtnText = 'Orte'; break;
			case 'ORT': this.anzahlText = 'Anzahl Schulen:'; this.sucheBtnText = 'Schulen'; break;
			default: this.anzahlText = ''; this.sucheBtnText = '';
		}
	}

	editItem() {
		if (this.katalogItem) {
			this.katalogFacade.switchToRenameKatalogItemEditor(this.katalogItem);
		}
	}

	openOrSearchChilds() {
		if (this.katalogItem) {
			this.katalogFacade.gotoChildItems(this.katalogItem);
		}
	}

}
