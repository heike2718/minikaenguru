import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgbDateStruct, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { UrkundenFacade } from '../urkunden.facade';
import { Subscription } from 'rxjs';
import { Kind, kindToString } from '@minikaenguru-ws/common-components';
import { KinderFacade } from '../../kinder/kinder.facade';
import { Urkundenart, Farbschema, UrkundenauftragEinzelkind, getLabelFarbe, getLabelUrkundenart, UrkundeDateModel } from '../urkunden.model';
import { NgForm } from '@angular/forms';

@Component({
	selector: 'mkv-urkundenauftrag',
	templateUrl: './urkundenauftrag.component.html',
	styleUrls: ['./urkundenauftrag.component.css']
})
export class UrkundenauftragComponent implements OnInit, OnDestroy {

	warntext = '';

	showWarntext = false;

	kind: Kind;

	nameKind: string;
	dateModel: NgbDateStruct;

	urkundeDateModel: UrkundeDateModel;

	urkundenart: Urkundenart;
	farbe: Farbschema;

	private selectedKindSubscription: Subscription;

	constructor(public urkundenFacade: UrkundenFacade,
		private kinderFacade: KinderFacade) {}

	ngOnInit(): void {

		this.warntext = this.urkundenFacade.warntext;
		this.showWarntext = this.warntext.length > 0;
		this.urkundeDateModel = this.urkundenFacade.getUrkundeDateModel();

		this.dateModel = this.urkundeDateModel.maxDate;

		this.urkundenart = 'TEILNAHME';

		this.selectedKindSubscription = this.kinderFacade.selectedKind$.subscribe(

			kind => {
				if (kind) {
					this.kind = kind;
					this.nameKind = kindToString(kind);
				}
			}
		);
	}

	ngOnDestroy(): void {

		if (this.selectedKindSubscription) {
			this.selectedKindSubscription.unsubscribe();
		}
	}


	onDateSelect($event: NgbDate): void {

		const dateString = this.urkundenFacade.calculateDateString($event);
		this.urkundeDateModel = {...this.urkundeDateModel, selectedDate: dateString};
	}

	onFormSubmit(form: NgForm): void {

		const value = form.value;

		const auftrag: UrkundenauftragEinzelkind = {
			kindUuid: this.kind.uuid,
			dateString: this.urkundeDateModel.selectedDate,
			farbschema: value['farbe'],
			urkundenart: value['urkundenart']
		};

		this.urkundenFacade.downloadUrkunde(auftrag);
	}

	onGotoKlassenliste(): void {
		this.urkundenFacade.gotoKlassenliste();
	}


	onCancel(): void {

		this.urkundenFacade.cancelEinzelurkunde();
	}

	zusammenfassung(): string {

		return this.urkundenFacade.zusammenfassungEinzelurkunde(this.farbe, this.urkundenart, this.nameKind, this.urkundeDateModel);
	}
}
