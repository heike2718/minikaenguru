import { Component, OnInit, OnDestroy, ViewChild, TemplateRef } from '@angular/core';
import { KlassenFacade } from '../klassen.facade';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Subscription } from 'rxjs';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { environment } from '../../../environments/environment';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LogService } from '@minikaenguru-ws/common-logging';
import { modalOptions } from '@minikaenguru-ws/common-components';

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

	anzahlKinder: number = 0;

	@ViewChild('loeschenWarndialog')
	loeschenWarndialog!: TemplateRef<HTMLElement>;

	private routeSubscription: Subscription = new Subscription();
	private schuleSubscription: Subscription = new Subscription();
	private anzahlLoesungszettelSubsciption = new Subscription();
	private anzahlKinderSubscription = new Subscription();

	constructor(private router: Router,
		private route: ActivatedRoute,
		public klassenFacade: KlassenFacade,
		private modalService: NgbModal,
		private lehrerFacade: LehrerFacade,
		private logger: LogService
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

		this.anzahlKinderSubscription = this.klassenFacade.anzahlKinder$.subscribe(
			anzahl => this.anzahlKinder = anzahl
		);

	}

	ngOnDestroy(): void {
		this.routeSubscription.unsubscribe();
		this.schuleSubscription.unsubscribe();
		this.anzahlLoesungszettelSubsciption.unsubscribe();
		this.anzahlKinderSubscription.unsubscribe();
	}


	addKlasse(): void {
		this.klassenFacade.startCreateKlasse();
	}

	gotoMeineSchulen(): void {
		this.router.navigateByUrl('/lehrer/schulen');
	}

	gotoUploadKlassenlisten(): void {
		
		if (this.schule) {
			this.klassenFacade.prepareShowUpload();
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

	deleteKlassen(): void {

		if (!this.schule) {
			return;
		}

		this.modalService.open(this.loeschenWarndialog, modalOptions).result.then((result) => {

			if (result === 'ja') {
				this.forceDeleteKlassen();
			}

		}, (reason) => {
			this.logger.debug('closed with reason=' + reason);
		});
	}

	forceDeleteKlassen(): void {

		if (this.schule) {
			this.klassenFacade.alleKlassenLoeschen(this.schule.kuerzel);
		}
	}

}
