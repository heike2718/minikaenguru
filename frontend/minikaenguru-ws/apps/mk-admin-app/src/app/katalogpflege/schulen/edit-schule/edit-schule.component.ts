import { Component, OnInit, OnDestroy } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { UntypedFormGroup, AbstractControl, UntypedFormBuilder, Validators } from '@angular/forms';
import { SchulePayload } from '../../katalogpflege.model';
import { Subscription } from 'rxjs';
import { initialSchuleEditorModel, SchuleEditorModel } from '../../+state/katalogpflege.reducer';
import { emailValidator } from '@minikaenguru-ws/common-components';
import { environment } from '../../../../environments/environment';

@Component({
	selector: 'mka-edit-schule',
	templateUrl: './edit-schule.component.html',
	styleUrls: ['./edit-schule.component.css']
})
export class EditSchuleComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	editSchuleInput$ = this.katalogFacade.editSchuleInput$;

	editSchuleForm!: UntypedFormGroup;

	name!: AbstractControl;

	kuerzel!: AbstractControl;

	nameOrt!: AbstractControl;

	kuerzelOrt!: AbstractControl;

	nameLand!: AbstractControl;

	kuerzelLand!: AbstractControl;

	emailAuftraggeber!: AbstractControl;

	submitDisabled: boolean = false;

	headlineText: string = '';

	submited: boolean = false;

	private schuleEditorModel: SchuleEditorModel = initialSchuleEditorModel;

	private editSchuleInputSubscription: Subscription = new Subscription();

	constructor(private fb: UntypedFormBuilder, private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.editSchuleInputSubscription = this.editSchuleInput$.subscribe(
			input => {
				if (input.schuleEditorModel !== undefined) {
					this.schuleEditorModel = input.schuleEditorModel;
					this.setFormValues();
				}
			}
		);
	}

	ngOnDestroy(): void {
		if (this.editSchuleInputSubscription) {
			this.editSchuleInputSubscription.unsubscribe();
		}
	}

	private initForm() {

		this.editSchuleForm = this.fb.group({
			'name': this.fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzel': this.fb.control({ value: '', disabled: true }),
			'nameOrt': this.fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzelOrt': this.fb.control({ value: '', disabled: true }),
			'nameLand': this.fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzelLand': this.fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(10)] }),
			'emailAuftraggeber': this.fb.control({value: ''}, {validators: [emailValidator]}),
		});

		this.name = this.editSchuleForm.controls['name'];
		this.kuerzel = this.editSchuleForm.controls['kuerzel'];
		this.kuerzelOrt = this.editSchuleForm.controls['kuerzelOrt'];
		this.nameOrt = this.editSchuleForm.controls['nameOrt'];
		this.kuerzelLand = this.editSchuleForm.controls['kuerzelLand'];
		this.nameLand = this.editSchuleForm.controls['nameLand'];
		this.emailAuftraggeber = this.editSchuleForm.controls['emailAuftraggeber'];

	}

	private setFormValues() {

		if (this.schuleEditorModel.schulePayload === undefined) {
			return;
		}

		this.editSchuleForm.reset();

		this.headlineText = this.schuleEditorModel.modusCreate ? 'Schule anlegen' : 'Schule ändern';
		this.editSchuleForm.setValue(this.schuleEditorModel.schulePayload);

		if (this.schuleEditorModel.kuerzelLandDisabled) {
			const input = this.editSchuleForm.get('kuerzelLand');
			if (input) {
				input.disable();
			}
		}
		if (this.editSchuleForm && this.schuleEditorModel.nameLandDisabled) {
			const input = this.editSchuleForm.get('nameLand');
			if (input) {
				input.disable();
			}
		}
		if (this.editSchuleForm && this.schuleEditorModel.nameOrtDisabled) {
			const input = this.editSchuleForm.get('nameOrt');
			if (input) {
				input.disable();
			}
		}
	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submited = true;

		const schulePayload: SchulePayload = {
			name: this.name ? this.name.value.trim() : '',
			kuerzel: this.kuerzel ? this.kuerzel.value : '',
			nameOrt: this.nameOrt ? this.nameOrt.value.trim() : '',
			kuerzelOrt: this.kuerzelOrt ? this.kuerzelOrt.value : '',
			nameLand: this.nameLand ? this.nameLand.value.trim() : '',
			kuerzelLand: this.kuerzelLand ? this.kuerzelLand.value : '',
			emailAuftraggeber: this.emailAuftraggeber && this.emailAuftraggeber.value !== '' ? this.emailAuftraggeber.value.trim() : null
		};

		if (this.schuleEditorModel.modusCreate) {
			this.katalogFacade.sendCreateSchule(schulePayload);
		} else {
			this.katalogFacade.sendRenameSchule(schulePayload);
		}
	}

	cancel(): void {

		this.setFormValues();
		this.submitDisabled = false;
		this.submited = false;

	}

	gotoKataloge(): void {
		this.katalogFacade.switchToKataloge();
	}
}
