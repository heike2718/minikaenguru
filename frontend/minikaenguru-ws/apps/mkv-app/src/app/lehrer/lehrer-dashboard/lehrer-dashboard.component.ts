import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { LehrerFacade } from '../lehrer.facade';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { LogoutService } from '../../services/logout.service';
import { environment } from '../../../environments/environment';
import { KlassenFacade } from '../../klassen/klassen.facade';
import { STORAGE_KEY_USER, User } from '@minikaenguru-ws/common-auth';
import { Subscription } from 'rxjs';
import { Schule, SchuleWithID } from '../schulen/schulen.model';

@Component({
	selector: 'mkv-lehrer-dashboard',
	templateUrl: './lehrer-dashboard.component.html',
	styleUrls: ['./lehrer-dashboard.component.css']
})
export class LehrerDashboardComponent implements OnInit, OnDestroy {

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;
	lehrer$ = this.lehrerFacade.lehrer$;
	schulen$ = this.lehrerFacade.schulen$;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';

	unterlagenDeutschUrl = environment.apiUrl + '/unterlagen/schulen/de';
	unterlagenEnglischUrl = environment.apiUrl + '/unterlagen/schulen/en';

	userIdRef?: string;

	schule: Schule | undefined;

	hatZugangZuUnterlagen: boolean = false;

	private zugangUnterlagenSubscription: Subscription = new Subscription();

	private schulenSubscription: Subscription = new Subscription();

	constructor(private lehrerFacade: LehrerFacade,
		private klassenFacade: KlassenFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private logoutService: LogoutService,
		private router: Router) { }

	ngOnInit(): void {

		const userSerialized = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);

		if (userSerialized) {
			const user: User = JSON.parse(userSerialized);
			this.userIdRef = user.idReference;
		}

		this.zugangUnterlagenSubscription = this.lehrerFacade.hatZugangZuUnterlagen$.subscribe(hat => {

			if (hat !== undefined) {
				this.hatZugangZuUnterlagen = hat;
			}
		});

		this.schulenSubscription = this.lehrerFacade.schulen$.subscribe(
			s => {
				if (s.length === 1) {
					this.schule = s[0].schule;
				}
			}
		);

		this.klassenFacade.resetState();

	}

	ngOnDestroy(): void {
		this.zugangUnterlagenSubscription.unsubscribe();
		this.schulenSubscription.unsubscribe();
	}

	gotoSchulen() {
		this.router.navigateByUrl('/lehrer/schulen');
	}

	gotoSchuleDashboard() {

	}

	gotoProfil() {
		this.logoutService.logout();
		window.location.href = environment.profileUrl;
	}

	gotoInfos(): void {
		this.router.navigateByUrl('/info');
	}

	gotoBewertung(): void {
		this.router.navigateByUrl('/feedback/wettbewerb');
	}

	toggleTextFeatureFlagAnzeigen(): void {
		this.textFeatureFlag = 'Das ist im Moment noch nicht möglich.';
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}

	changeAboNewsletter(): void {
		this.lehrerFacade.changeAboNewsletter();
	}

	addSchule(): void {
		this.lehrerFacade.neueSchulsuche();
		this.router.navigateByUrl('/lehrer/schulen/add-schule')
	}
}
