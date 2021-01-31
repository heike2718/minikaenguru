import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { LehrerFacade } from '../lehrer.facade';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { LogoutService } from '../../services/logout.service';
import { environment } from '../../../environments/environment';
import { KlassenFacade } from '../../klassen/klassen.facade';
import { STORAGE_KEY_USER, User } from '@minikaenguru-ws/common-auth';
import { Subscribable, Subscription } from 'rxjs';

@Component({
	selector: 'mkv-lehrer-dashboard',
	templateUrl: './lehrer-dashboard.component.html',
	styleUrls: ['./lehrer-dashboard.component.css']
})
export class LehrerDashboardComponent implements OnInit, OnDestroy {

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;
	lehrer$ = this.lehrerFacade.lehrer$;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';

	unterlagenDeutschUrl  = environment.apiUrl + '/unterlagen/schulen/de';
	unterlagenEnglischUrl  = environment.apiUrl + '/unterlagen/schulen/en';

	userIdRef: string;

	hatZugangZuUnterlagen = false;

	private zugangUnterlagenSubscription: Subscription;

	constructor(private lehrerFacade: LehrerFacade,
		private klassenFacade: KlassenFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private logoutService: LogoutService,
		private router: Router) { }

	ngOnInit(): void {

		const user: User = JSON.parse(localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER));
		this.userIdRef = user.idReference;

		this.zugangUnterlagenSubscription = this.lehrerFacade.hatZugangZuUnterlagen$.subscribe(hat => {
			this.hatZugangZuUnterlagen = hat;
		});

		this.klassenFacade.resetState();

	}

	ngOnDestroy(): void {

		if (this.zugangUnterlagenSubscription) {
			this.zugangUnterlagenSubscription.unsubscribe();
		}
	}

	gotoSchulen() {
		this.router.navigateByUrl('/lehrer/schulen');
	}

	gotoProfil() {
		this.logoutService.logout();
		window.location.href = environment.profileUrl;
	}

	gotoInfos(): void {
		this.router.navigateByUrl('/info');
	}

	toggleTextFeatureFlagAnzeigen(): void {
		this.textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}

	changeAboNewsletter(): void {
		this.lehrerFacade.changeAboNewsletter();
	}
}
