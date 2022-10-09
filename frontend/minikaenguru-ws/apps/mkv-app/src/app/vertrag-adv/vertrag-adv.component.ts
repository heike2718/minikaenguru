import { Component, OnInit, OnDestroy } from '@angular/core';
import { UntypedFormGroup, UntypedFormControl, Validators, UntypedFormBuilder } from '@angular/forms';
import { VertragAdvFacade } from './vertrag-adv.facade';
import { Subscription } from 'rxjs';
import { VertragAdvEditorModel } from './vertrag-adv.model';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { DownloadButtonModel } from '@minikaenguru-ws/common-components';
import { LehrerFacade } from '../lehrer/lehrer.facade';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mkv-vertrag-adv',
	templateUrl: './vertrag-adv.component.html',
	styleUrls: ['./vertrag-adv.component.css']
})
export class VertragAdvComponent implements OnInit, OnDestroy {

	vertragAdvForm: UntypedFormGroup;

	vertragAdvEditorModel$ = this.vertragAdvFacade.vertragAdvEditorModel$;
	submitDisabled$ = this.vertragAdvFacade.submitDisabled$;

	selectedSchule$ = this.lehrerFacade.selectedSchule$;

	vertragstextBtnModel: DownloadButtonModel = {
		id: '',
		url: environment.apiUrl + '/adv/vertragstext',
		dateiname: 'vertrag-Auftragsverarbeitung',
		mimetype: 'pdf',
		buttonLabel: 'Vertragstext herunterladen',
		tooltip: 'allgemeinen Vertragstext herunterladen (PDF)',
		class: 'btn btn-outline-dark w-100 ml-1'
	};

	vertragBtnModel?: DownloadButtonModel;

	initialEditorModel?: VertragAdvEditorModel;

	schulnameFormControl: UntypedFormControl;

	plzFormControl: UntypedFormControl;

	ortFormControl: UntypedFormControl;

	strasseFormControl: UntypedFormControl;

	hausnummerFormControl: UntypedFormControl;

	formChangeSubscription: Subscription = new Subscription();

	advEditorModelSubscription: Subscription = new Subscription();

	constructor(private fb: UntypedFormBuilder,
		private router: Router,
		private vertragAdvFacade: VertragAdvFacade,
		private lehrerFacade: LehrerFacade,
		private logger: LogService) {

		this.schulnameFormControl = new UntypedFormControl({ value: '', disabled: true }),
			this.plzFormControl = new UntypedFormControl({ value: '' }, Validators.required);
		this.ortFormControl = new UntypedFormControl({ value: '', disabled: true }),
			this.strasseFormControl = new UntypedFormControl({ value: '' }, Validators.required);
		this.hausnummerFormControl = new UntypedFormControl({ value: '' }, Validators.required);


		this.vertragAdvForm = this.fb.group({
			schulname: this.schulnameFormControl,
			plz: this.plzFormControl,
			ort: this.ortFormControl,
			strasse: this.strasseFormControl,
			hausnummer: this.hausnummerFormControl
		});
	}

	ngOnInit(): void {

		this.advEditorModelSubscription = this.vertragAdvEditorModel$.subscribe(

			vertrag => {
				if (vertrag) {
					this.initialEditorModel = { ...vertrag };
					this.vertragAdvForm.patchValue(vertrag);


					this.vertragBtnModel = {
						id: vertrag.schulkuerzel,
						url: environment.apiUrl + '/adv/' + vertrag.schulkuerzel,
						dateiname: 'Vertrag-Auftragsverarbeitung-Minikaenguru',
						mimetype: 'pdf',
						buttonLabel: 'Vertrag herunterladen',
						tooltip: 'Vertrag Auftragsverarbeitung herunterladen (PDF)',
						class: 'btn btn-outline-dark w-100 ml-1'
					};

					this.formChangeSubscription = this.vertragAdvForm.statusChanges.subscribe(
						_s => {

							this.vertragAdvFacade.validateForm(this.vertragAdvForm);
						});
				}
			}
		);
	}

	ngOnDestroy(): void {
		this.advEditorModelSubscription.unsubscribe();
		this.formChangeSubscription.unsubscribe();
	}

	submit(): void {

		if (!this.initialEditorModel) {
			this.logger.debug('initialEditorModel is undefined');
			return;
		}

		const formValue: VertragAdvEditorModel = this.vertragAdvForm.value;
		const vertrag = { ...formValue, schulkuerzel: this.initialEditorModel.schulkuerzel, schulname: this.initialEditorModel.schulname, ort: this.initialEditorModel.ort };
		this.vertragAdvFacade.submitVertrag(vertrag);
	}

	gotoSchule(): void {

		if (!this.initialEditorModel) {
			this.logger.debug('initialEditorModel is undefined');
			return;
		}
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.initialEditorModel.schulkuerzel);
	}
}
