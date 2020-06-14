import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { TeilnahmenFacade } from '../teilnahmen/teilnahmen.facade';

@Component({
	selector: 'mkv-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {


	user$ = this.authService.user$;
	aktuellerWettbewerb$ = this.teilnahmenFacade.aktuellerWettbewerb$;
	hatZugangZuUnterlagen$ = this.teilnahmenFacade.hatZugangZuUnterlagen$;


	constructor(private authService: AuthService,
		private teilnahmenFacade: TeilnahmenFacade,
		private router: Router) { }

	gotoSchulen() {
		this.router.navigateByUrl('/schulen');
	}

	gotoDSGVO(): void {
		console.log('navigate to DSGVO');
	}

	gotoDownloadUnterlagen() {
		console.log('hier gehts zu den Unterlagen: Achtung - vorher Status abfragen, ob angemeldet und freigeschaltet!');
	}

	gotoProfil() {
		console.log('hier gehts zum Profil');
	}

	gotoInfos(): void {
		this.router.navigateByUrl('/info');
	}
}
