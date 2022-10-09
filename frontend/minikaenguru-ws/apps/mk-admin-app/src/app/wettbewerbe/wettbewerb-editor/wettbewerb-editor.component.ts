import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
// import { DOCUMENT } from '@angular/common';
import { UntypedFormBuilder, Validators, UntypedFormGroup, UntypedFormControl } from '@angular/forms';
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

	devMode = environment.envName === 'DEV';

	wettbewerbForm!: UntypedFormGroup;

	wettbewerbEditorModel$ = this.wettbewerbFacade.wettbewerbEditorModel$;

	jahrFormControl!: UntypedFormControl;

	statusFormControl!: UntypedFormControl;

	wettbewerbsbeginnFormControl!: UntypedFormControl;

	wettbewerbsendeFormControl!: UntypedFormControl;

	datumFreischaltungLehrerFormControl!: UntypedFormControl;

	datumFreischaltungPrivatFormControl!: UntypedFormControl;

	loesungsbuchstabenIkidsFormControl!: UntypedFormControl;

	loesungsbuchstabenKlasse1FormControl!: UntypedFormControl;

	loesungsbuchstabenKlasse2FormControl!: UntypedFormControl;

	private wettbewerbEditorModelSubscription: Subscription = new Subscription();

	private saveOutcomeSubscription: Subscription = new Subscription();

	private initialWettbewerbGuiModel = {} as WettbewerbEditorModel;

	constructor(private fb: UntypedFormBuilder,
		private wettbewerbFacade: WettbewerbFacade,
		// @Inject(DOCUMENT) private document: Document,
		private router: Router,
		private messageService: MessageService,
		private logger: LogService) { }

	ngOnInit(): void {

		this.wettbewerbEditorModelSubscription = this.wettbewerbEditorModel$.subscribe(
			wb => {

				if (wb) {
					this.initialWettbewerbGuiModel = {...wb};
				}

				this.jahrFormControl = new UntypedFormControl({ value: '' }, Validators.required);
				this.statusFormControl = new UntypedFormControl({ value: '', disabled: true });
				this.wettbewerbsbeginnFormControl = new UntypedFormControl({ value: '' }, Validators.required);
				this.wettbewerbsendeFormControl = new UntypedFormControl({ value: '' }, Validators.required);
				this.datumFreischaltungLehrerFormControl = new UntypedFormControl({ value: '' }, Validators.required);
				this.datumFreischaltungPrivatFormControl = new UntypedFormControl({ value: '' }, Validators.required);
				this.loesungsbuchstabenIkidsFormControl = new UntypedFormControl({ value: '' });
				this.loesungsbuchstabenKlasse1FormControl = new UntypedFormControl({ value: '' });
				this.loesungsbuchstabenKlasse2FormControl = new UntypedFormControl({ value: '' });


				this.wettbewerbForm = this.fb.group({
					jahr: this.jahrFormControl,
					status: this.statusFormControl,
					wettbewerbsbeginn: this.wettbewerbsbeginnFormControl,
					wettbewerbsende: this.wettbewerbsendeFormControl,
					datumFreischaltungLehrer: this.datumFreischaltungLehrerFormControl,
					datumFreischaltungPrivat: this.datumFreischaltungPrivatFormControl,
					loesungsbuchstabenIkids: this.loesungsbuchstabenIkidsFormControl,
					loesungsbuchstabenKlasse1: this.loesungsbuchstabenKlasse1FormControl,
					loesungsbuchstabenKlasse2: this.loesungsbuchstabenKlasse2FormControl
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
		this.wettbewerbEditorModelSubscription.unsubscribe();
		this.saveOutcomeSubscription.unsubscribe();
	}

	submitDisabled(): boolean {
		return this.wettbewerbForm.invalid;
	}

	onSubmit() {
		const formValue: WettbewerbEditorModel = this.wettbewerbForm.value;

		const neuerWettbewerb: WettbewerbEditorModel= {...formValue,
			datumFreischaltungLehrer: formValue.datumFreischaltungLehrer ? formValue.datumFreischaltungLehrer.trim() : '',
			datumFreischaltungPrivat: formValue.datumFreischaltungPrivat ? formValue.datumFreischaltungPrivat.trim() : '',
			wettbewerbsbeginn: formValue.wettbewerbsbeginn ? formValue.wettbewerbsbeginn.trim() : '',
			wettbewerbsende: formValue.wettbewerbsende ? formValue.wettbewerbsende.trim() : '',
			status: this.initialWettbewerbGuiModel.status, // wird sowieso ignoriert
			loesungsbuchstabenIkids: formValue.loesungsbuchstabenIkids.trim(),
			loesungsbuchstabenKlasse1: formValue.loesungsbuchstabenKlasse1.trim(),
			loesungsbuchstabenKlasse2: formValue.loesungsbuchstabenKlasse2.trim()
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
