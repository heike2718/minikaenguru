import { Component, Input, ViewChild, TemplateRef, OnInit, OnDestroy } from '@angular/core';
import { Klasse, modalOptions } from '@minikaenguru-ws/common-components';
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
	loeschenWarndialog!: TemplateRef<HTMLElement>;

	@Input()
	klasse!: Klasse;

	anzahlKinder = 0;
	anzahlLoesungszettel = 0;

	private anzahlLoesungszettelSubsciption = new Subscription();
	private anzahlKinderSubscription = new Subscription();

	constructor(private router: Router,
		private modalService: NgbModal,
		public klassenFacade: KlassenFacade,
		private logger: LogService
	) { }

	ngOnInit(): void {

		this.anzahlKinderSubscription = this.klassenFacade.getAnzahlKinderInKlasse(this.klasse.uuid).subscribe(
			anz => this.anzahlKinder = anz
		);

		this.anzahlLoesungszettelSubsciption = this.klassenFacade.getAnzahlLoesungszettelInKlasse(this.klasse.uuid).subscribe(
			anz => this.anzahlLoesungszettel = anz
		);
	}

	ngOnDestroy(): void {
		this.anzahlKinderSubscription.unsubscribe();
		this.anzahlLoesungszettelSubsciption.unsubscribe();
	}

	onCheckboxKlasseKorrigiertClicked(event: boolean) {
		if (event) {
			this.klassenFacade.markKlasseKorrigiert(this.klasse.uuid);
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
