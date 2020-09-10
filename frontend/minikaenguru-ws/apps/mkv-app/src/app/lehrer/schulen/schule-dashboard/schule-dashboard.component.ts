import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';
import { WettbewerbFacade } from '../../../wettbewerb/wettbewerb.facade';
import { AuthService, User } from '@minikaenguru-ws/common-auth';
import { Subscription } from 'rxjs';
import { Schule } from '../schulen.model';
import { LogService } from '@minikaenguru-ws/common-logging';
import { TeilnahmenFacade } from '../../../teilnahmen/teilnahmen.facade';

@Component({
	selector: 'mkv-schule-dashboard',
	templateUrl: './schule-dashboard.component.html',
	styleUrls: ['./schule-dashboard.component.css']
})
export class SchuleDashboardComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	details$ = this.lehrerFacade.schuleDetails$;

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	loading$ = this.lehrerFacade.loading$;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';

	vertragAdvUrl: string;
	vertragAdvDateiname = 'Vertrag-Auftragsdatenverarbeitung-Minikaenguru';
	vertragAdvMimetype = 'pdf';
	vertragAdvCardTitle = 'DSGVO';
	vertragAdvSubtext = 'Vertrag Auftragsdatenverarbeitung herunterladen';


	private user: User;

	schule: Schule;

	private userSubscription: Subscription;

	private schuleSubscription: Subscription;

	private teilnahmenummerSubscription: Subscription;

	private teilnahmenSelected: boolean;

	constructor(private router: Router,
		private lehrerFacade: LehrerFacade,
		private authService: AuthService,
		private wettbewerbFacade: WettbewerbFacade,
		private teilnahmenFacade: TeilnahmenFacade,
		private logger: LogService) {
	}

	ngOnInit(): void {

		this.teilnahmenSelected = false;

		this.userSubscription = this.authService.user$.subscribe(
			u => this.user = u
		);

		this.schuleSubscription = this.lehrerFacade.selectedSchule$.subscribe(
			s => {
				this.schule = s;

				if (this.schule) {
					this.vertragAdvUrl = environment.apiUrl + '/adv/' + this.schule.kuerzel;
					this.logger.debug(JSON.stringify(this.schule));
				} else {
					this.vertragAdvUrl = '';
				}
			}
		);

		this.teilnahmenummerSubscription = this.teilnahmenFacade.teilnahmenummerAndName$.subscribe(

			theNummer => {
				if (this.teilnahmenSelected && theNummer !== undefined) {
					this.router.navigateByUrl('teilnahmen');
				}
			}

		);
	}

	ngOnDestroy(): void {
		if (this.userSubscription) {
			this.userSubscription.unsubscribe();
		}

		if (this.schuleSubscription) {
			this.schuleSubscription.unsubscribe();
		}

		if (this.teilnahmenummerSubscription) {
			this.teilnahmenummerSubscription.unsubscribe();
		}
	}

	gotoVertragAdv(): void {
		this.textFeatureFlag = 'DSGVO im Moment noch nicht möglich, kommt aber im Herbst 2020.';
		this.textFeatureFlagAnzeigen = true;
	}

	anmelden(): void {
		this.lehrerFacade.schuleAnmelden(this.schule, this.user);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

	gotoTeilnahmen(): void {
		this.teilnahmenSelected = true;
		this.teilnahmenFacade.selectTeilnahmenummer(this.schule.kuerzel, this.schule.name);
	}

	gotoAuswertung(): void {
		this.textFeatureFlagAnzeigen = true;
		this.textFeatureFlag = 'Auswertungen sind im Moment noch nicht möglich, kommt aber Anfang 2021.';
	}

	backToSchulen(): void {
		this.lehrerFacade.resetSelection();
		this.router.navigateByUrl('/lehrer/schulen');
	}

	toggleTextFeatureFlagAnzeigen(): void {
		this.textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}


}
