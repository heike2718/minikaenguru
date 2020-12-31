import { Component, OnInit, OnDestroy } from '@angular/core';
import { KlassenFacade } from '../klassen.facade';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Observable, Subscription } from 'rxjs';
import { Klasse } from '@minikaenguru-ws/common-components';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { environment } from '../../../environments/environment';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
	selector: 'mkv-klassen-list',
	templateUrl: './klassen-list.component.html',
	styleUrls: ['./klassen-list.component.css']
})
export class KlassenListComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	// klassen$: Observable<Klasse[]> = this.klassenFacade.klassen$;
	// anzahlKlassen$: Observable<number> = this.klassenFacade.anzahlKlassen$;

	schule: Schule;

	tooltipBtnSchuluebersicht: string;

	private routeSubscription: Subscription;
	private schuleSubscription: Subscription;

	constructor(private router: Router,
		private route: ActivatedRoute,
		public klassenFacade: KlassenFacade,
		private lehrerFacade: LehrerFacade
		) { }

	ngOnInit(): void {

		this.tooltipBtnSchuluebersicht = 'Übersicht';

		this.routeSubscription = this.route.paramMap.subscribe(

			paramMap => {
				const param = paramMap.get('schulkuerzel');
				if (param) {
					this.klassenFacade.loadKlassen(param);
				}
			}

		);

		this.schuleSubscription = this.lehrerFacade.selectedSchule$.subscribe(
			s => {
				if (s) {
					this.schule = s;
					this.tooltipBtnSchuluebersicht = 'Übersicht ' + this.schule.name;
				} else {
					this.router.navigateByUrl('/lehrer/schulen');
				}
			}
		);

	}

	ngOnDestroy(): void {
		if (this.routeSubscription) {
			this.routeSubscription.unsubscribe();
		}
		if (this.schuleSubscription) {
			this.schuleSubscription.unsubscribe();
		}
	}


	addKlasse(): void {
		this.klassenFacade.startCreateKlasse();
	}

	gotoMeineSchulen(): void {
		this.router.navigateByUrl('/lehrer/schulen');
	}

	gotoSchulauswertung(): void {
		this.router.navigateByUrl('/schulauswertung');
	}

	gotoDashboard(): void {

		let url = '/lehrer/dashboard';
		if (this.schule) {
			url = '/lehrer/schule-dashboard/' + this.schule.kuerzel;
		}

		this.router.navigateByUrl(url);
	}

}
