import { Component, OnInit, Input } from '@angular/core';
import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';
import { environment } from '../../../environments/environment';

@Component({
	selector: 'mkv-teilnahme-card',
	templateUrl: './teilnahme-card.component.html',
	styleUrls: ['./teilnahme-card.component.css']
})
export class TeilnahmeCardComponent implements OnInit {



	devMode = !environment.production;
	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';

	downloadUrl: string;
	downloadDateiname = 'test';
	downloadMimetype = 'pdf';
	downloadLabel = 'Statistik';
	downloadTooltip = 'Statistik generieren und herunterladen';


	@Input() teilnahme: AnonymisierteTeilnahme;
	constructor() { }

	ngOnInit(): void {

		this.downloadUrl = environment.apiUrl + '/adv/6XOA2A11';
	}

	getStatistic(): void {
		console.log('jetzt Statistik für die Teilnahme mit identifier erstellen: ' + JSON.stringify(this.teilnahme.identifier));
		this.textFeatureFlagAnzeigen = true;
	}

	toggleTextFeatureFlagAnzeigen(): void {
		this.textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}
}
