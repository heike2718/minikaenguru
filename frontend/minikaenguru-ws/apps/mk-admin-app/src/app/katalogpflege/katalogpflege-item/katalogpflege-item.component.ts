import { Component, OnInit, Input } from '@angular/core';
import { KatalogpflegeItem } from '../katalogpflege.model';

@Component({
	selector: 'mka-katalogpflege-item',
	templateUrl: './katalogpflege-item.component.html',
	styleUrls: ['./katalogpflege-item.component.css']
})
export class KatalogpflegeItemComponent implements OnInit {


	@Input()
	katalogItem: KatalogpflegeItem;

	anzahlText: string;

	constructor() { }

	ngOnInit(): void {

		switch (this.katalogItem.typ) {
			case 'LAND': this.anzahlText = 'Anzahl Orte:'; break;
			case 'ORT': this.anzahlText = 'Anzahl Schulen:'; break;
			default: this.anzahlText = '';
		}
	}

}
