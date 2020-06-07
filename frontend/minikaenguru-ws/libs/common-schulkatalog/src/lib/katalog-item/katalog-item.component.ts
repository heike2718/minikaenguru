import { Component, OnInit, Input, Inject } from '@angular/core';
import { KatalogItem } from '../domain/entities';
import { InternalFacade } from '../application-services/internal.facade';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';

@Component({
	selector: 'mk-katalog-item',
	templateUrl: './katalog-item.component.html',
	styleUrls: ['./katalog-item.component.css']
})
export class KatalogItemComponent implements OnInit {

	@Input()
	katalogItem: KatalogItem;

	anzahlText: string;

	constructor(@Inject(SchulkatalogConfigService) public readonly config, private internalFacade: InternalFacade) { }

	ngOnInit() {

		switch (this.katalogItem.typ) {
			case 'LAND': this.anzahlText = 'Anzahl Orte:'; break;
			case 'ORT': this.anzahlText = 'Anzahl Schulen:'; break;
			default: this.anzahlText = '';
		}

	}

	selectTheItem() {
		this.internalFacade.selectKatalogItem(this.katalogItem);
	}
}

