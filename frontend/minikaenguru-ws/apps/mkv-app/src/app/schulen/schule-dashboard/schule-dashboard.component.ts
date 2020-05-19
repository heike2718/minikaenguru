import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Schule, Person, kollegenAsString, SchuleDashboardModel } from '../schulen.model';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-schule-dashboard',
	templateUrl: './schule-dashboard.component.html',
	styleUrls: ['./schule-dashboard.component.css']
})
export class SchuleDashboardComponent implements OnInit {



	schule: Schule;
	kollegium: string;

	devMode = !environment.production;


	constructor(private router: Router) {

		const personen = [];
		personen.push({
			fullName: 'Darth Vader'
		} as Person);

		// wird nachgeladen durch einen Sideeffect.
		const dashboardModel = {
			kollegium: personen,
			anzahlTeilnahmen: 4,
			angemeldetDurch: {
				fullName: 'Darth Vader'
			} as Person
		} as SchuleDashboardModel;

		this.schule = {
			name: 'Elefantenschule',
			ort: 'Zoo',
			land: 'Bayern',
			aktuellAngemeldet: true,
			kuerzel: 'UIGHDG65',
			dashboardModel: dashboardModel
		};

		this.kollegium = kollegenAsString(dashboardModel.kollegen);
	}

	ngOnInit(): void {
	}

	backToSchulen(): void {
		this.router.navigate(['/schulen']);
	}

}
