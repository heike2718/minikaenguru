import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { selectedWettbewerb, saveOutcome } from '../+state/wettbewerbe.selectors';
import { Subscription } from 'rxjs';
import { WettbewerbEditorModel, Wettbewerb } from '../wettbewerbe.model';
import { WettbewerbeService } from '../../services/wettbewerbe.service';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Router } from '@angular/router';
import { MessageService, Message } from '@minikaenguru-ws/common-messages';

@Component({
	selector: 'mka-wettbewerb-editor',
	templateUrl: './wettbewerb-editor.component.html',
	styleUrls: ['./wettbewerb-editor.component.css']
})
export class WettbewerbEditorComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	wettbewerbForm: FormGroup;

	wettbewerb$ = this.store.select(selectedWettbewerb);

	private wettbewerbSubscription: Subscription;

	private saveOutcomeSubscription: Subscription;

	private wettbewerbGuiModel = {} as WettbewerbEditorModel;

	private wettbewerb: Wettbewerb;

	constructor(private fb: FormBuilder,
		private store: Store<AppState>,
		private wettbewerbeService: WettbewerbeService,
		@Inject(DOCUMENT) private document: Document,
		private router: Router,
		private messageService: MessageService,
		private logger: LogService) { }

	jahrFormControl: FormControl;

	statusFormControl: FormControl;

	wettbewerbsbeginnFormControl: FormControl;

	wettbewerbsendeFormControl: FormControl;

	datumFreischaltungLehrerFormControl: FormControl;

	datumFreischaltungPrivatFormControl: FormControl;

	ngOnInit(): void {

		this.wettbewerbSubscription = this.wettbewerb$.subscribe(
			wb => {
				this.wettbewerb = wb;
				this.initGuiModel(wb);

				this.jahrFormControl = new FormControl({ value: '' }, Validators.required);
				if (this.wettbewerb.jahr === 0) {

					this.statusFormControl = new FormControl({ value: '', disabled: true }, Validators.required);
				} else {
					this.statusFormControl = new FormControl({ value: '', disabled: false }, Validators.required);
				}

				this.wettbewerbsbeginnFormControl = new FormControl({ value: '' });
				this.wettbewerbsendeFormControl = new FormControl({ value: '' }, Validators.required);
				this.datumFreischaltungLehrerFormControl = new FormControl({ value: '' }, Validators.required);
				this.datumFreischaltungPrivatFormControl = new FormControl({ value: '' }, Validators.required);


				this.wettbewerbForm = this.fb.group({
					jahr: this.jahrFormControl,
					status: this.statusFormControl,
					wettbewerbsbeginn: this.wettbewerbsbeginnFormControl,
					wettbewerbsende: this.wettbewerbsendeFormControl,
					datumFreischaltungLehrer: this.datumFreischaltungLehrerFormControl,
					datumFreischaltungPrivat: this.datumFreischaltungPrivatFormControl
				});


				this.wettbewerbForm.patchValue(this.wettbewerbGuiModel);
			}
		);

		this.saveOutcomeSubscription = this.store.select(saveOutcome).subscribe(

			outcome => {
				if (outcome !== undefined) {
					this.messageService.showMessage(outcome);
				}
			}
		);
	}

	private initGuiModel(wb: Wettbewerb): void {
		this.wettbewerbGuiModel.jahr = wb.jahr;
		this.wettbewerbGuiModel.status = wb.status;
		if (wb.wettbewerbsbeginn) {
			this.wettbewerbGuiModel.wettbewerbsbeginn = wb.wettbewerbsbeginn;

		} else {
			this.wettbewerbGuiModel.wettbewerbsbeginn = '';
		}

		if (wb.wettbewerbsende) {
			this.wettbewerbGuiModel.wettbewerbsende = wb.wettbewerbsende;
		} else {

			this.wettbewerbGuiModel.wettbewerbsende = '';
		}

		if (wb.datumFreischaltungLehrer) {
			this.wettbewerbGuiModel.datumFreischaltungLehrer = wb.datumFreischaltungLehrer;
		} else {
			this.wettbewerbGuiModel.datumFreischaltungLehrer = '';
		}

		if (wb.datumFreischaltungPrivat) {
			this.wettbewerbGuiModel.datumFreischaltungPrivat = wb.datumFreischaltungPrivat;
		} else {
			this.wettbewerbGuiModel.datumFreischaltungPrivat = '';
		}
	}

	private mergeFormValue(): Wettbewerb {

		const formValue: WettbewerbEditorModel = this.wettbewerbForm.value;
		return {
			jahr: formValue.jahr,
			status: formValue.status !== undefined ? formValue.status : this.wettbewerb.status,
			wettbewerbsbeginn: formValue.wettbewerbsbeginn ? formValue.wettbewerbsbeginn.trim() : null,
			wettbewerbsende: formValue.wettbewerbsende.trim(),
			datumFreischaltungLehrer: formValue.datumFreischaltungLehrer.trim(),
			datumFreischaltungPrivat: formValue.datumFreischaltungPrivat.trim()
		};
	}

	ngOnDestroy(): void {
		if (this.wettbewerbSubscription) {
			this.wettbewerbSubscription.unsubscribe();
		}
		if (this.saveOutcomeSubscription) {
			this.saveOutcomeSubscription.unsubscribe();
		}
	}

	submitDisabled(): boolean {
		return this.wettbewerbForm.invalid;
	}

	onSubmit() {
		const neuerWettbewerb = this.mergeFormValue();
		this.logger.debug(JSON.stringify(neuerWettbewerb));
		this.wettbewerbeService.saveWettbewerb(neuerWettbewerb);
	}

	onCancel() {
		this.initGuiModel(this.wettbewerb);
		this.wettbewerbForm.patchValue(this.wettbewerbGuiModel);
	}

	gotoWettbewerbe(): void {
		this.router.navigateByUrl('/wettbewerbe');
	}
}
