import { Component, OnInit, OnDestroy } from '@angular/core';
import { KlassenFacade } from '../klassen.facade';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Observable, Subscription } from 'rxjs';
import { Klasse } from '@minikaenguru-ws/common-components';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-klassen-list',
	templateUrl: './klassen-list.component.html',
	styleUrls: ['./klassen-list.component.css']
})
export class KlassenListComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	klassen$: Observable<Klasse[]> = this.klassenFacade.klassen$;
	anzahlKlassen$: Observable<number> = this.klassenFacade.anzahlKlassen$;
	selectedSchule$: Observable<Schule> = this.lehrerFacade.selectedSchule$;

	private schule: Schule;

	private schuleSubscription: Subscription;

	constructor(private router: Router,
		private klassenFacade: KlassenFacade,
		private lehrerFacade: LehrerFacade) { }

	ngOnInit(): void {

		this.schuleSubscription = this.selectedSchule$.subscribe(
			s => this.schule = s
		);

	}

	ngOnDestroy(): void {
		if (this.schuleSubscription) {
			this.schuleSubscription.unsubscribe();
		}
	}


	addKlasse(): void {
		console.log('jetzt zum klasse-editor wechseln');
	}

	gotoDashboard(): void {

		let url = '/lehrer/dashboard';
		if (this.schule) {
			url = '/lehrer/schule-dashboard/' + this.schule.kuerzel;
		}

		this.router.navigateByUrl(url);
	}

}
