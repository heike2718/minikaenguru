import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LehrerFacade } from '../lehrer.facade';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';

@Component({
	selector: 'mkv-lehrer-dashboard',
	templateUrl: './lehrer-dashboard.component.html',
	styleUrls: ['./lehrer-dashboard.component.css']
})
export class LehrerDashboardComponent implements OnInit {

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;
	hatZugangZuUnterlagen$ = this.lehrerFacade.hatZugangZuUnterlagen$;


	constructor(private lehrerFacade: LehrerFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private router: Router) { }

	ngOnInit(): void {
	}

	gotoSchulen() {
		this.router.navigateByUrl('/lehrer/schulen');
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
