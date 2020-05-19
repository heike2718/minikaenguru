import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Schule, Person } from '../schulen.model';

@Component({
	selector: 'mkv-schulen-list',
	templateUrl: './schulen-list.component.html',
	styleUrls: ['./schulen-list.component.css']
})
export class SchulenListComponent implements OnInit {


	devMode = !environment.production;

	schulen: Schule[];

	constructor() {

		this.schulen = [];

		this.schulen.push({
			name: 'Blümchenschule',
			ort: 'Wiesental',
			land: 'Bayern',
			aktuellAngemeldet: false,
			kuerzel: 'HJFFFIG5',
			anzahlTeilnahmen: 4
		} as Schule);

		const personen = [];
		personen.push({
			fullName: 'Darth Vader'
		} as Person);

		this.schulen.push({
			name: 'Elefantenschule',
			ort: 'Zoo',
			land: 'Bayern',
			aktuellAngemeldet: true,
			kuerzel: 'UIGHDG65',
			kollegium: personen,
			angemeldetDurch: {
				fullName: 'Darth Vader'
			} as Person,
			anzahlTeilnahmen: 2
		} as Schule);

	 }

	ngOnInit(): void {
	}

	addSchule(): void {
		console.log('hier neue Schule auswählen lassen')
	}

}
