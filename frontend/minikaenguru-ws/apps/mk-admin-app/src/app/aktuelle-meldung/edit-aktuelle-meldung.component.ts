import { Component, OnInit, OnDestroy } from '@angular/core';
import { AktuelleMeldungFacade } from './aktuelle-meldung.facade';
import { Subscription } from 'rxjs';
import { AktuelleMeldung } from './aktuelle-meldung.model';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mka-aktuelle-meldung',
	templateUrl: './aktuelle-meldung.component.html',
	styleUrls: ['./aktuelle-meldung.component.css']
})
export class EditAktuelleMeldungComponent implements OnInit, OnDestroy {


	private aktuelleMeldungSubscription: Subscription;

	private initialMeldung: AktuelleMeldung;

	aktuelleMeldungGeladen$ = this.aktuelleMeldungFacade.aktuelleMeldungGeladen$;
	aktuelleMeldung: AktuelleMeldung;

	constructor(private fb: FormBuilder,
		private aktuelleMeldungFacade: AktuelleMeldungFacade,
		private logger: LogService) { }

	ngOnInit(): void {

		this.aktuelleMeldungFacade.ladeAktuelleMeldung();

		this.aktuelleMeldungSubscription = this.aktuelleMeldungFacade.aktuelleMeldung$.subscribe(

			m => {
				this.initialMeldung = { ...m };
				this.aktuelleMeldung = { ...m };
			}
		);
	}

	submitDisabled(): boolean {

		if (!this.aktuelleMeldung) {
			return true;
		}

		if (this.aktuelleMeldung.text.trim().length === 0) {
			return true;
		}

		if (this.aktuelleMeldung.text === this.initialMeldung.text) {
			return true;
		}

		return false;
	}

	deleteDisabled(): boolean {

		if (!this.aktuelleMeldung) {
			return true;
		}

		if (this.aktuelleMeldung.text.length === 0) {
			return true;
		}

		return false;
	}

	ngOnDestroy(): void {

		if (this.aktuelleMeldungSubscription) {
			this.aktuelleMeldungSubscription.unsubscribe();
		}
	}

	onSubmit(): void {

		const meldung: AktuelleMeldung = {...this.aktuelleMeldung, text: this.aktuelleMeldung.text.trim()};

		this.logger.debug(JSON.stringify(meldung));
		this.aktuelleMeldungFacade.aktuelleMeldungSpeichern(meldung);

	}

	onCancel() {
		this.aktuelleMeldung = { ...this.initialMeldung };
	}

	onDelete(): void {
		this.aktuelleMeldungFacade.aktuelleMeldungLoeschen();
	}
}
