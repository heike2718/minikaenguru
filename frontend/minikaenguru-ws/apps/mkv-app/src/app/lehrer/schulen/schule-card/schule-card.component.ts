import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Schule } from '../schulen.model';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';
import { WettbewerbFacade } from '../../../wettbewerb/wettbewerb.facade';
import { Wettbewerb } from '../../../wettbewerb/wettbewerb.model';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mkv-schule-card',
	templateUrl: './schule-card.component.html',
	styleUrls: ['./schule-card.component.css']
})
export class SchuleCardComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	@Input()
	schule: Schule;

	wettbewerb: Wettbewerb;

	private aktuellerWettbewerbSubscription: Subscription;

	constructor(private router: Router, private lehrerFacade: LehrerFacade, private wettbewerbFacade: WettbewerbFacade) { }

	ngOnInit(): void {

		this.aktuellerWettbewerbSubscription = this.wettbewerbFacade.aktuellerWettbewerb$.subscribe(
			w => this.wettbewerb = w
		);

	}

	ngOnDestroy(): void {
		if (this.aktuellerWettbewerbSubscription) {
			this.aktuellerWettbewerbSubscription.unsubscribe();
		}
	}


	selectSchule(): void {
		this.lehrerFacade.selectSchule(this.schule);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

}
