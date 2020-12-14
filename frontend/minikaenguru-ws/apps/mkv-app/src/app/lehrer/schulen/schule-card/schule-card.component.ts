import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Schule } from '../schulen.model';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';
import { WettbewerbFacade } from '../../../wettbewerb/wettbewerb.facade';

@Component({
	selector: 'mkv-schule-card',
	templateUrl: './schule-card.component.html',
	styleUrls: ['./schule-card.component.css']
})
export class SchuleCardComponent implements OnInit {

	devMode = !environment.production;

	@Input()
	schule: Schule;

	wettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	constructor(private router: Router, private lehrerFacade: LehrerFacade, private wettbewerbFacade: WettbewerbFacade) { }

	ngOnInit(): void {}

	selectSchule(): void {
		this.lehrerFacade.selectSchule(this.schule);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

	gotoAuswertung(): void {
		this.lehrerFacade.selectSchule(this.schule);
		this.router.navigateByUrl('/klassen/' + this.schule.kuerzel);
	}

	vonSchuleAbmelden(): void {
		this.lehrerFacade.removeSchule(this.schule);
	}

}
