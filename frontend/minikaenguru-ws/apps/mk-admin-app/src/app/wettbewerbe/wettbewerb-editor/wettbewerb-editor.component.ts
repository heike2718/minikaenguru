import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
// import { DOCUMENT } from '@angular/common';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { Subscription } from 'rxjs';
import { WettbewerbEditorModel } from '../wettbewerbe.model';
import { WettbewerbFacade } from '../../services/wettbewerb.facade';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Router } from '@angular/router';
import { MessageService } from '@minikaenguru-ws/common-messages';

@Component({
	selector: 'mka-wettbewerb-editor',
	templateUrl: './wettbewerb-editor.component.html',
	styleUrls: ['./wettbewerb-editor.component.css']
})
export class WettbewerbEditorComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	wettbewerbForm: FormGroup;

	wettbewerbEditorModel$ = this.wettbewerbFacade.wettbewerbEditorModel$;

	private wettbewerbSubscription: Subscription;

	private saveOutcomeSubscription: Subscription;

	private initialWettbewerbGuiModel = {} as WettbewerbEditorModel;

	constructor(private fb: FormBuilder,
		private wettbewerbFacade: WettbewerbFacade,
		// @Inject(DOCUMENT) private document: Document,
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

		this.wettbewerbSubscription = this.wettbewerbEditorModel$.subscribe(
			wb => {
				this.initialWettbewerbGuiModel = {...wb};

				this.jahrFormControl = new FormControl({ value: '' }, Validators.required);
				this.statusFormControl = new FormControl({ value: '', disabled: true });
				this.wettbewerbsbeginnFormControl = new FormControl({ value: '' }, Validators.required);
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


				this.wettbewerbForm.patchValue(this.initialWettbewerbGuiModel);
			}
		);

		this.saveOutcomeSubscription = this.wettbewerbFacade.saveOutcome$.subscribe(

			outcome => {
				if (outcome !== undefined) {
					this.messageService.showMessage(outcome);
				}
			}
		);
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
		const formValue: WettbewerbEditorModel = this.wettbewerbForm.value;
		const neuerWettbewerb: WettbewerbEditorModel= {...formValue,
			datumFreischaltungLehrer: formValue.datumFreischaltungLehrer.trim(),
			datumFreischaltungPrivat: formValue.datumFreischaltungPrivat.trim(),
			wettbewerbsbeginn: formValue.wettbewerbsbeginn.trim(),
			wettbewerbsende: formValue.wettbewerbsende.trim(),
			status: this.initialWettbewerbGuiModel.status // wird sowieso ignorier
		}
		this.logger.debug('neuerWettbewerb: ' + JSON.stringify(neuerWettbewerb));
		this.wettbewerbFacade.saveWettbewerb(neuerWettbewerb, this.initialWettbewerbGuiModel.jahr === 0);
	}

	onCancel() {
		this.wettbewerbForm.patchValue(this.initialWettbewerbGuiModel);
	}

	gotoWettbewerbe(): void {
		this.router.navigateByUrl('/wettbewerbe');
	}
}