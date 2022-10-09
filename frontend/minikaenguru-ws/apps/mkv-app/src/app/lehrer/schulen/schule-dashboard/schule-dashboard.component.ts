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

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber bis März.';

	vertragAdvModel!: DownloadCardModel;

	private user?: User;

	schule?: Schule;

	private userSubscription: Subscription = new Subscription();

	private schuleSubscription: Subscription = new Subscription();

	private teilnahmenummerSubscription: Subscription = new Subscription();

	private teilnahmenSelected: boolean = false;

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
			u => {
				if (u) {
					this.user = u;
				}
			}
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
						subtext: 'Vertrag Auftragsverarbeitung herunterladen (PDF)',
						dateiname: 'Vertrag-Auftragsverarbeitung-Minikaenguru',
						mimetype: 'pdf'
					};
				} else {
					this.vertragAdvModel = {
						id: '',
						url: '',
						cardTitle: 'DSGVO',
						subtext: 'Vertrag Auftragsverarbeitung herunterladen (PDF)',
						dateiname: 'Vertrag-Auftragsverarbeitung-Minikaenguru',
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
		this.userSubscription.unsubscribe();
		this.schuleSubscription.unsubscribe();
		this.teilnahmenummerSubscription.unsubscribe();
	}

	gotoVertragAdv(): void {
		if (this.schule) {
			this.vertragAdvFacade.setSelectedSchule(this.schule);
			this.router.navigateByUrl('/adv');
		} else {
			this.logger.debug('selectedSchule was undefined');
		}
	}

	anmelden(): void {

		if (!this.schule) {
			this.logger.debug('selectedSchule was undefined');
			return;
		}

		if (!this.user) {
			this.logger.debug('user was undefined');
			return;
		}

		this.lehrerFacade.schuleAnmelden(this.schule, this.user);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

	gotoTeilnahmen(): void {

		if (!this.schule) {
			this.logger.debug('selectedSchule was undefined');
			return;
		}

		this.teilnahmenSelected = true;
		this.teilnahmenFacade.selectTeilnahmenummer(this.schule.kuerzel, this.schule.name);
	}

	gotoKlassenliste(): void {

		if (!this.schule) {
			this.logger.debug('selectedSchule was undefined');
			return;
		}

		this.textFeatureFlagAnzeigen = false;
		this.router.navigateByUrl('/klassen/' + this.schule.kuerzel);
	}

	gotoUploadAuswertung(): void {
		this.router.navigateByUrl('lehrer/upload-auswertung');
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

	vonSchuleAbmelden(): void {

		if (!this.schule) {
			this.logger.debug('selectedSchule was undefined');
			return;
		}

		this.lehrerFacade.removeSchule(this.schule);
		this.gotoDashboard();
	}
}
