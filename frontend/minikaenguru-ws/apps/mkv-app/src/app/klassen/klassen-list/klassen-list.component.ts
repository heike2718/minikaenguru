import { Component, OnInit, OnDestroy } from '@angular/core';
import { KlassenFacade } from '../klassen.facade';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Subscription } from 'rxjs';
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

	schule?: Schule;

	tooltipBtnSchuluebersicht: string = '';

	anzahlLoesungszettel: number = 0;

	private routeSubscription: Subscription = new Subscription();
	private schuleSubscription: Subscription = new Subscription();
	private anzahlLoesungszettelSubsciption = new Subscription();

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

		this.anzahlLoesungszettelSubsciption = this.klassenFacade.anzahlLoesungszettel$.subscribe(
			anzahl => this.anzahlLoesungszettel = anzahl
		);

	}

	ngOnDestroy(): void {
		this.routeSubscription.unsubscribe();
		this.schuleSubscription.unsubscribe();
		this.anzahlLoesungszettelSubsciption.unsubscribe();
	}


	addKlasse(): void {
		this.klassenFacade.startCreateKlasse();
	}

	gotoMeineSchulen(): void {
		this.router.navigateByUrl('/lehrer/schulen');
	}

	gotoUploadKklassenlisten(): void {
		
		if (this.schule) {
			this.router.navigateByUrl('/klassen/uploads/' + this.schule.kuerzel);
		}
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
