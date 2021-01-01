import { Component, Input, ViewChild, TemplateRef, OnInit, OnDestroy } from '@angular/core';
import { Klasse } from '@minikaenguru-ws/common-components';
import { Router } from '@angular/router';
import { KlassenFacade } from '../klassen.facade';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mkv-klasse-details',
	templateUrl: './klasse-details.component.html',
	styleUrls: ['./klasse-details.component.css']
})
export class KlasseDetailsComponent implements OnInit, OnDestroy {

	@ViewChild('loeschenWarndialog')
	loeschenWarndialog: TemplateRef<HTMLElement>;

	@Input()
	klasse: Klasse;

	anzahlKinder: number;
	anzahlLoesungszettel: number;

	private selectedKlasseSubscription: Subscription;

	constructor(private router: Router,
		private modalService: NgbModal,
		public klassenFacade: KlassenFacade,
		private logger: LogService
	) { }

	ngOnInit(): void {

		this.anzahlKinder = this.klasse.anzahlKinder;
		this.anzahlLoesungszettel = this.klasse.anzahlLoesungszettel;

		this.selectedKlasseSubscription = this.klassenFacade.selectedKlasse$.subscribe(
			klasse => {
				if (klasse && klasse.uuid === this.klasse.uuid) {
					this.anzahlKinder = klasse.anzahlKinder;
					this.anzahlLoesungszettel = klasse.anzahlLoesungszettel;
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

			this.modalService.open(this.loeschenWarndialog, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {

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
