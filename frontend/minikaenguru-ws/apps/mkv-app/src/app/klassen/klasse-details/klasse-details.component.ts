import { Component, Input, ViewChild, TemplateRef, OnInit, OnDestroy } from '@angular/core';
import { Klasse } from '@minikaenguru-ws/common-components';
import { Router } from '@angular/router';
import { KlassenFacade } from '../klassen.facade';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Subscription } from 'rxjs';
import { modalOptions } from '../../shared/utils';

@Component({
	selector: 'mkv-klasse-details',
	templateUrl: './klasse-details.component.html',
	styleUrls: ['./klasse-details.component.css']
})
export class KlasseDetailsComponent implements OnInit, OnDestroy {

	@ViewChild('loeschenWarndialog')
	loeschenWarndialog!: TemplateRef<HTMLElement>;

	@Input()
	klasse!: Klasse;

	anzahlKinder: number = 0;
	anzahlLoesungszettel: number = 0;

	private selectedKlasseSubscription: Subscription = new Subscription();

	constructor(private router: Router,
		private modalService: NgbModal,
		public klassenFacade: KlassenFacade,
		private logger: LogService
	) { }

	ngOnInit(): void {

		this.anzahlKinder = this.klasse.anzahlKinder? this.klasse.anzahlKinder : 0;
		this.anzahlLoesungszettel = this.klasse.anzahlLoesungszettel? this.klasse.anzahlLoesungszettel : 0;

		this.selectedKlasseSubscription = this.klassenFacade.selectedKlasse$.subscribe(
			klasse => {
				if (klasse && klasse.uuid === this.klasse.uuid) {
					this.anzahlKinder = klasse.anzahlKinder ? klasse.anzahlKinder : 0;
					this.anzahlLoesungszettel = klasse.anzahlLoesungszettel ? klasse.anzahlLoesungszettel : 0;
				}
			}
		);
	}

	ngOnDestroy(): void {

		if(this.selectedKlasseSubscription ) {
			this.selectedKlasseSubscription.unsubscribe();
		}

	}


	editKlasse(): void {
		this.klassenFacade.editKlasse(this.klasse);
		const url = '/klasse-editor/' + this.klasse.uuid;
		this.router.navigateByUrl(url);
	}

	gotoKinder(): void {
		this.klassenFacade.insertUpdateKinder(this.klasse);
	}

	deleteKlasse(): void {

		if (this.klasse.anzahlKinder && this.klasse.anzahlKinder > 0) {

			this.modalService.open(this.loeschenWarndialog, modalOptions).result.then((result) => {

				if (result === 'ja') {
					this.forceDeleteKlasse();
				}

			}, (reason) => {
				this.logger.debug('closed with reason=' + reason);
			});

		} else {
			this.forceDeleteKlasse();
		}
	}

	private forceDeleteKlasse(): void {
		this.klassenFacade.deleteKlasse(this.klasse);
	}

}
