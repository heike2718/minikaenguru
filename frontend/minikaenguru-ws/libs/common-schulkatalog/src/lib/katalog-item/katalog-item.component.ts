import { Component, OnInit, Input } from '@angular/core';
import { KatalogItem } from '../domain/entities';
import { SchulkatalogFacade } from '../application-services/schulkatalog.facade';

@Component({
	selector: 'mk-katalog-item',
	templateUrl: './katalog-item.component.html',
	styleUrls: ['./katalog-item.component.css']
})
export class KatalogItemComponent implements OnInit {

	@Input()
	katalogItem: KatalogItem;

	@Input()
	devMode: boolean;

	anzahlText: string;

	constructor(private schulkatalogFacade: SchulkatalogFacade) { }

	ngOnInit() {

		switch (this.katalogItem.typ) {
			case 'LAND': this.anzahlText = 'Anzahl Orte:'; break;
			case 'ORT': this.anzahlText = 'Anzahl Schulen:'; break;
			default: this.anzahlText = '';
		}

	}

	selectTheItem() {
		this.schulkatalogFacade.selectKatalogItem(this.katalogItem);
	}
}

