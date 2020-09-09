import { Component, OnInit, OnDestroy } from '@angular/core';
import { PrivatveranstalterFacade } from '../privatveranstalter.facade';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { TeilnahmenFacade } from '../../teilnahmen/teilnahmen.facade';
import { Subscription } from 'rxjs';
import { LogoutService } from '../../services/logout.service';

@Component({
	selector: 'mkv-privat-dashboard',
	templateUrl: './privat-dashboard.component.html',
	styleUrls: ['./privat-dashboard.component.css']
})
export class PrivatDashboardComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	aktuelleTeilnahmeGeladen$ = this.veranstalterFacade.aktuelleTeilnahmeGeladen$;

	privatveranstalter$ = this.veranstalterFacade.veranstalter$;

	loading$ = this.veranstalterFacade.loading$;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';

	private veranstalterSubscription: Subscription;

	private teilnahmenummerSubscription: Subscription;

	private teilnahmenummer: string;

	private teilnahmenSelected: boolean;

	constructor(private veranstalterFacade: PrivatveranstalterFacade,
		private teilnahmenFacade: TeilnahmenFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private logoutService: LogoutService,
		private router: Router
	) { }

	ngOnInit(): void {

		this.teilnahmenSelected = false;

		this.veranstalterSubscription = this.privatveranstalter$.subscribe(
			veranstalter => {
				if (veranstalter) {
					this.teilnahmenummer = veranstalter.teilnahmenummer;
				}
			}
		);

		this.teilnahmenummerSubscription = this.teilnahmenFacade.teilnahmenummerAndName$.subscribe(

			theNummer => {
				if (this.teilnahmenSelected && theNummer !== undefined) {
					this.teilnahmenSelected = false;
					this.router.navigateByUrl('teilnahmen');
				}
			}

		);

	}

	ngOnDestroy(): void {
		if (this.veranstalterSubscription) {
			this.veranstalterSubscription.unsubscribe();
		}

		if (this.teilnahmenummerSubscription) {
			this.teilnahmenummerSubscription.unsubscribe();
		}

	}

	gotoTeilnahmen(): void {
		this.teilnahmenSelected = true;
		this.teilnahmenFacade.selectTeilnahmenummer(this.teilnahmenummer, '');
	}

	gotoAuswertung(): void {
		this.textFeatureFlagAnzeigen = true;
		this.textFeatureFlag = 'Auswertungen sind im Moment noch nicht möglich, kommt aber Anfang 2021.';
	}

	gotoDownloadUnterlagen() {
		console.log('hier gehts zu den Unterlagen: Achtung - vorher Status abfragen, ob angemeldet und freigeschaltet!');
	}

	gotoProfil() {
		this.logoutService.logout();
		window.location.href = environment.profileUrl;
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

	changeAboNewsletter(): void {
		this.veranstalterFacade.changeAboNewsletter();
	}

}
