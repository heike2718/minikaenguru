import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Schule, Person } from '../schulen.model';

@Component({
	selector: 'mkv-schule-dashboard',
	templateUrl: './schule-dashboard.component.html',
	styleUrls: ['./schule-dashboard.component.css']
})
export class SchuleDashboardComponent implements OnInit {



	schule: Schule;

	devMode = !environment.production;


	constructor() {

		const personen = [];
		personen.push({
			fullName: 'Darth Vader'
		} as Person);

		this.schule = {
			name: 'Elefantenschule',
			ort: 'Zoo',
			land: 'Bayern',
			aktuellAngemeldet: true,
			kuerzel: 'UIGHDG65',
			kollegium: personen,
			angemeldetDurch: 'Darth Vader'
		};
	}

	ngOnInit(): void {
	}

}
