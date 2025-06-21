import { Component, OnInit, OnDestroy } from '@angular/core';
import { PrivatveranstalterFacade } from '../privatveranstalter.facade';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { TeilnahmenFacade } from '../../teilnahmen/teilnahmen.facade';
import { Subscription } from 'rxjs';
import { LogoutService } from '../../services/logout.service';
import { STORAGE_KEY_USER, User } from '@minikaenguru-ws/common-auth';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mkv-privat-dashboard',
	templateUrl: './privat-dashboard.component.html',
	styleUrls: ['./privat-dashboard.component.css']
})
export class PrivatDashboardComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	aktuelleTeilnahmeGeladen$ = this.veranstalterFacade.aktuelleTeilnahmeGeladen$;

	privatveranstalter$ = this.veranstalterFacade.veranstalter$;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';

	userIdRef?: string;

	unterlagenDeutschUrl  = environment.apiUrl + '/unterlagen/privat/de';
	unterlagenEnglischUrl  = environment.apiUrl + '/unterlagen/privat/en';

	hatZugangZuUnterlagen = false;

	private veranstalterSubscription: Subscription = new Subscription();

	private teilnahmenummerSubscription: Subscription = new Subscription();

	private teilnahmenummer?: string;

	private teilnahmenSelected: boolean = false;

	constructor(private veranstalterFacade: PrivatveranstalterFacade,
		private teilnahmenFacade: TeilnahmenFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private logoutService: LogoutService,
		private logger: LogService,
		private router: Router
	) { }

	ngOnInit(): void {

		const obj : string | null = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);
		if (obj) {
			const user: User = JSON.parse(obj);
			this.userIdRef = user.idReference;
		}
		this.teilnahmenSelected = false;

		this.veranstalterSubscription = this.privatveranstalter$.subscribe(
			veranstalter => {
				if (veranstalter) {
					this.teilnahmenummer = veranstalter.teilnahmenummer;
					this.hatZugangZuUnterlagen = veranstalter.hatZugangZuUnterlagen;
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
		this.veranstalterSubscription.unsubscribe();
		this.teilnahmenummerSubscription.unsubscribe();
	}

	gotoTeilnahmen(): void {

		if (!this.teilnahmenummer) {
			this.logger.debug('teilnahmenummer was undefined');
			return;
		}

		this.teilnahmenSelected = true;
		this.teilnahmenFacade.selectTeilnahmenummer(this.teilnahmenummer, '');
	}

	gotoAuswertung(): void {
		this.textFeatureFlagAnzeigen = false;
		this.router.navigateByUrl('/kinder/' + this.teilnahmenummer);
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
