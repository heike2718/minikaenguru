import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
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

	vertragAdvForm: FormGroup;

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

	schulnameFormControl: FormControl;

	plzFormControl: FormControl;

	ortFormControl: FormControl;

	strasseFormControl: FormControl;

	hausnummerFormControl: FormControl;

	formChangeSubscription: Subscription = new Subscription();

	advEditorModelSubscription: Subscription = new Subscription();

	constructor(private fb: FormBuilder,
		private router: Router,
		private vertragAdvFacade: VertragAdvFacade,
		private lehrerFacade: LehrerFacade,
		private logger: LogService) {

		this.schulnameFormControl = new FormControl({ value: '', disabled: true }),
			this.plzFormControl = new FormControl({ value: '' }, Validators.required);
		this.ortFormControl = new FormControl({ value: '', disabled: true }),
			this.strasseFormControl = new FormControl({ value: '' }, Validators.required);
		this.hausnummerFormControl = new FormControl({ value: '' }, Validators.required);


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
