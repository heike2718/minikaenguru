import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LehrerFacade } from '../lehrer.facade';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { ThrowStmt } from '@angular/compiler';
import { LogoutService } from '../../services/logout.service';
import { environment } from '../../../environments/environment';

@Component({
	selector: 'mkv-lehrer-dashboard',
	templateUrl: './lehrer-dashboard.component.html',
	styleUrls: ['./lehrer-dashboard.component.css']
})
export class LehrerDashboardComponent implements OnInit {

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;
	lehrer$ = this.lehrerFacade.lehrer$;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';

	constructor(private lehrerFacade: LehrerFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private logoutService: LogoutService,
		private router: Router) { }

	ngOnInit(): void {
	}

	gotoSchulen() {
		this.router.navigateByUrl('/lehrer/schulen');
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

	toggleTextFeatureFlagAnzeigen(): void {
		this.textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}

	changeAboNewsletter(): void {
		this.lehrerFacade.changeAboNewsletter();
	}
}
