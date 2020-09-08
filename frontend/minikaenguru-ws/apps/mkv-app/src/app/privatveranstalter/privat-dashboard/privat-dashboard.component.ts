import { Component } from '@angular/core';
import { PrivatveranstalterFacade } from '../privatveranstalter.facade';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { Router } from '@angular/router';
import { environment } from 'apps/mkv-app/src/environments/environment';

@Component({
	selector: 'mkv-privat-dashboard',
	templateUrl: './privat-dashboard.component.html',
	styleUrls: ['./privat-dashboard.component.css']
})
export class PrivatDashboardComponent {

	devMode = !environment.production;

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	aktuelleTeilnahmeGeladen$ = this.veranstalterFacade.aktuelleTeilnahmeGeladen$;

	privatveranstalter$ = this.veranstalterFacade.veranstalter$;

	loading$ = this.veranstalterFacade.loading$;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';

	constructor(private veranstalterFacade: PrivatveranstalterFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private router: Router
	) { }

	gotoTeilnahmen(): void {
		this.router.navigateByUrl('/privat/teilnahmen');
	}

	gotoAuswertung(): void {
		this.textFeatureFlagAnzeigen = true;
		this.textFeatureFlag = 'Auswertungen sind im Moment noch nicht möglich, kommt aber Anfang 2021.';
	}

	gotoDownloadUnterlagen() {
		console.log('hier gehts zu den Unterlagen: Achtung - vorher Status abfragen, ob angemeldet und freigeschaltet!');
	}

	gotoProfil() {
		this.textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';
		this.textFeatureFlagAnzeigen = true;
	}

	gotoInfos(): void {
		this.router.navigateByUrl('/info');
	}

	anmelden(): void {
		this.veranstalterFacade.privatveranstalterAnmelden();
	}

	toggleTextFeatureFlagAnzeigen(): void {
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}

}
