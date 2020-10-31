import { Component, OnInit, Input, ViewChild, TemplateRef } from '@angular/core';
import { Kind } from '@minikaenguru-ws/common-components';
import { Privatveranstalter } from '../../wettbewerb/wettbewerb.model';
import { Router } from '@angular/router';
import { PrivatveranstalterFacade } from '../../privatveranstalter/privatveranstalter.facade';
import { PrivatauswertungFacade } from '../privatauswertung.facade';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mkv-kind-details',
	templateUrl: './kind-details.component.html',
	styleUrls: ['./kind-details.component.css']
})
export class KindDetailsComponent implements OnInit {

	@ViewChild('loeschenWarndialog')
	loeschenWarndialog: TemplateRef<HTMLElement>;

	@Input()
	kind: Kind

	@Input()
	veranstalter: Privatveranstalter;

	titel: string;

	constructor(private router: Router,
		private modalService: NgbModal,
		private privatauswertungFacade: PrivatauswertungFacade,
		private logger: LogService
	) { }

	ngOnInit(): void {

		this.titel = this.createTitel();
	}


	private createTitel(): string {

		let result = this.kind.vorname;

		if (this.kind.nachname) {
			result = result + ' ' + this.kind.nachname;
		}
		if (this.kind.zusatz) {
			result = result + ' (' + this.kind.zusatz + ')'
		}

		return result;
	}


	editKind(): void {
		this.privatauswertungFacade.editKind(this.kind);
		const url = '/privatauswertung/kind-editor/' + this.kind.uuid;
		this.router.navigateByUrl(url);
	}

	deleteKind(): void {

		this.modalService.open(this.loeschenWarndialog, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {

			if (result === 'ja') {
				this.privatauswertungFacade.deleteKind(this.kind.uuid);
			}

		}, (reason) => {
			this.logger.debug('closed with reason=' + reason);
		});
	}

	editLoesungszettel(): void {

	}

}
