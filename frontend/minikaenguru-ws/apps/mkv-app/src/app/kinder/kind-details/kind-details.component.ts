import { Component, OnInit, Input, ViewChild, TemplateRef, OnDestroy } from '@angular/core';
import { Kind } from '@minikaenguru-ws/common-components';
import { Privatveranstalter, AbstractVeranstalter } from '../../wettbewerb/wettbewerb.model';
import { Router } from '@angular/router';
import { PrivatveranstalterFacade } from '../../privatveranstalter/privatveranstalter.facade';
import { KinderFacade } from '../kinder.facade';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Subscription } from 'rxjs';
import { KlassenFacade } from '../../klassen/klassen.facade';

@Component({
	selector: 'mkv-kind-details',
	templateUrl: './kind-details.component.html',
	styleUrls: ['./kind-details.component.css']
})
export class KindDetailsComponent implements OnInit, OnDestroy {

	@ViewChild('loeschenWarndialog')
	loeschenWarndialog: TemplateRef<HTMLElement>;

	@Input()
	kind: Kind

	@Input()
	veranstalter: AbstractVeranstalter;

	selectedKlasse$ = this.klassenFacade.selectedKlasse$;

	private klasseSubscription: Subscription;

	private klasseUuid: string;

	titel: string;

	constructor(private router: Router,
		private modalService: NgbModal,
		private kinderFacade: KinderFacade,
		private klassenFacade: KlassenFacade,
		private logger: LogService
	) { }

	ngOnInit(): void {

		this.titel = this.createTitel();

		this.klasseSubscription = this.selectedKlasse$.subscribe(
			kl => {
				if (kl) {
					this.klasseUuid = kl.uuid;
				}
			}
		);
	}

	ngOnDestroy(): void {
		if (this.klasseSubscription) {
			this.klasseSubscription.unsubscribe();
		}
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
		this.kinderFacade.editKind(this.kind);
		const url = '/kinder/kind-editor/' + this.kind.uuid;

		if (this.klasseUuid) {
			this.router.navigate([url], { queryParams: { klasseUuid: this.klasseUuid } });
		} else {
			this.router.navigateByUrl(url);
		}
	}

	deleteKind(): void {

		this.modalService.open(this.loeschenWarndialog, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {

			if (result === 'ja') {
				this.kinderFacade.deleteKind(this.kind.uuid, this.klasseUuid);
			}

		}, (reason) => {
			this.logger.debug('closed with reason=' + reason);
		});
	}

	editLoesungszettel(): void {

	}

}
