import { Component, OnInit } from '@angular/core';
import { PrivatveranstalterFacade } from '../privatveranstalter.facade';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { Router } from '@angular/router';
import { environment } from 'apps/mkv-app/src/environments/environment';

@Component({
	selector: 'mkv-privat-dashboard',
	templateUrl: './privat-dashboard.component.html',
	styleUrls: ['./privat-dashboard.component.css']
})
export class PrivatDashboardComponent implements OnInit {

	devMode = !environment.production;

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;
	veranstalter$ = this.veranstalterFacade.veranstalter$;
	loading$ = this.veranstalterFacade.loading$;

	constructor(private veranstalterFacade: PrivatveranstalterFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private router: Router
	) { }

	ngOnInit(): void {
	}

	gotoTeilnahmen(): void {
		console.log('navigate to teilnahmen');
	}

	gotoAuswertung(): void {
		console.log('navigate to auswertung');
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

	anmelden(): void {
		this.veranstalterFacade.privatveranstalterAnmelden();
	}

}
