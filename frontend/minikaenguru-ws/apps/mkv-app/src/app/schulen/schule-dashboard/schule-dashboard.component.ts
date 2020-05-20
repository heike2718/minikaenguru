import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Schule, Person, SchuleDashboardModel } from '../schulen.model';
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

		const dashboardModel = {
			kollegen: 'Darth Vader',
			anzahlTeilnahmen: 4,
			angemeldetDurch: 'Darth Vader'
		} as SchuleDashboardModel;

		this.schule = {
			name: 'Elefantenschule',
			ort: 'Zoo',
			land: 'Bayern',
			aktuellAngemeldet: true,
			kuerzel: 'UIGHDG65',
			dashboardModel: dashboardModel
		};

		this.kollegium = dashboardModel.kollegen;
	}

	ngOnInit(): void {
	}

	backToSchulen(): void {
		this.router.navigate(['/schulen']);
	}

}
