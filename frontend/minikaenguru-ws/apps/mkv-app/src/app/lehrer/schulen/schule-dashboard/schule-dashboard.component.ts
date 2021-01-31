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
import { VertragAdvFacade } from '../../../vertrag-adv/vertrag-adv.facade';
import { DownloadCardModel } from '@minikaenguru-ws/common-components';
import { KlassenFacade } from '../../../klassen/klassen.facade';

@Component({
	selector: 'mkv-schule-dashboard',
	templateUrl: './schule-dashboard.component.html',
	styleUrls: ['./schule-dashboard.component.css']
})
export class SchuleDashboardComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	details$ = this.lehrerFacade.schuleDetails$;

	hatZugangZuUnterlagen$ = this.lehrerFacade.hatZugangZuUnterlagen$;

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	loading$ = this.lehrerFacade.loading$;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber bis März.';

	vertragAdvModel: DownloadCardModel;

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
		private vertragAdvFacade: VertragAdvFacade,
		private klassenFacade: KlassenFacade,
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

					this.logger.debug(JSON.stringify(this.schule));


					this.vertragAdvModel = {
						id: this.schule.kuerzel,
						url: environment.apiUrl + '/adv/' + this.schule.kuerzel,
						cardTitle: 'DSGVO',
						subtext: 'Vertrag Auftragsdatenverarbeitung herunterladen (PDF)',
						dateiname: 'Vertrag-Auftragsdatenverarbeitung-Minikaenguru',
						mimetype: 'pdf'
					};
				} else {
					this.vertragAdvModel = {
						id: '',
						url: '',
						cardTitle: 'DSGVO',
						subtext: 'Vertrag Auftragsdatenverarbeitung herunterladen (PDF)',
						dateiname: 'Vertrag-Auftragsdatenverarbeitung-Minikaenguru',
						mimetype: 'pdf'
					};
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
		this.vertragAdvFacade.setSelectedSchule(this.schule);
		this.router.navigateByUrl('/adv');
	}

	anmelden(): void {
		this.lehrerFacade.schuleAnmelden(this.schule, this.user);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

	gotoTeilnahmen(): void {
		this.teilnahmenSelected = true;
		this.teilnahmenFacade.selectTeilnahmenummer(this.schule.kuerzel, this.schule.name);
	}

	gotoKlassenliste(): void {
		this.textFeatureFlagAnzeigen = false;
		this.router.navigateByUrl('/klassen/' + this.schule.kuerzel);
	}

	gotoKlassenlisteHochladen(): void {
		// TODO
		this.textFeatureFlagAnzeigen = true;
	}

	backToSchulen(): void {
		this.klassenFacade.resetState();
		this.lehrerFacade.resetSelection();
		this.router.navigateByUrl('/lehrer/schulen');
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/lehrer/dashboard');
	}

	toggleTextFeatureFlagAnzeigen(): void {
		this.textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber bis März.';
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}


}
