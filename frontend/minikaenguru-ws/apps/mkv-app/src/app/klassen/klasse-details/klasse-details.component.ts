import { Component, OnInit, Input, ViewChild, TemplateRef } from '@angular/core';
import { Klasse } from '@minikaenguru-ws/common-components';
import { Router } from '@angular/router';
import { KlassenFacade } from '../klassen.facade';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mkv-klasse-details',
	templateUrl: './klasse-details.component.html',
	styleUrls: ['./klasse-details.component.css']
})
export class KlasseDetailsComponent implements OnInit {

	@ViewChild('loeschenWarndialog')
	loeschenWarndialog: TemplateRef<HTMLElement>;


	@Input()
	klasse: Klasse;

	constructor(private router: Router,
		private modalService: NgbModal,
		private klassenFacade: KlassenFacade,
		private logger: LogService
	) { }

	ngOnInit(): void { }


	editKlasse(): void {
		this.klassenFacade.editKlasse(this.klasse);
		const url = '/klasse-editor/' + this.klasse.uuid;
		this.router.navigateByUrl(url);
	}

	gotoKinder(): void {
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
