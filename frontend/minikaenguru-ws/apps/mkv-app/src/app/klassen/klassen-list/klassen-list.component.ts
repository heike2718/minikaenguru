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

	devMode = !environment.production;

	klassen$: Observable<Klasse[]> = this.klassenFacade.klassen$;
	anzahlKlassen$: Observable<number> = this.klassenFacade.anzahlKlassen$;

	schule: Schule;

	private routeSubscription: Subscription;
	private schuleSubscription: Subscription;
	private klassenSubscription: Subscription;

	constructor(private router: Router,
		private route: ActivatedRoute,
		private klassenFacade: KlassenFacade,
		private lehrerFacade: LehrerFacade
		) { }

	ngOnInit(): void {

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
				} else {
					this.router.navigateByUrl('/lehrer/schulen');
				}
			}
		);

		this.klassenSubscription = this.klassen$.subscribe(
			klassen => {
				for (let ind = 0; ind < klassen.length; ind++) {
					console.log(JSON.stringify(klassen[ind]));
				}
			}
		)


	}

	ngOnDestroy(): void {
		if (this.routeSubscription) {
			this.routeSubscription.unsubscribe();
		}
		if (this.schuleSubscription) {
			this.schuleSubscription.unsubscribe();
		}
		if (this.klassenSubscription) {
			this.klassenSubscription.unsubscribe();
		}
	}


	addKlasse(): void {
		this.klassenFacade.startCreateKlasse();
	}

	gotoDashboard(): void {

		let url = '/lehrer/dashboard';
		if (this.schule) {
			url = '/lehrer/schule-dashboard/' + this.schule.kuerzel;
		}

		this.router.navigateByUrl(url);
	}

}
