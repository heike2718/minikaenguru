import { Component, OnInit, OnDestroy } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { SchulePayload } from '../../katalogpflege.model';
import { Subscription } from 'rxjs';
import { SchuleEditorModel } from '../../+state/katalogpflege.reducer';
import { emailValidator } from '@minikaenguru-ws/common-components';

@Component({
	selector: 'mka-edit-schule',
	templateUrl: './edit-schule.component.html',
	styleUrls: ['./edit-schule.component.css']
})
export class EditSchuleComponent implements OnInit, OnDestroy {

	editSchuleInput$ = this.katalogFacade.editSchuleInput$;

	editSchuleForm: FormGroup;

	name: AbstractControl;

	kuerzel: AbstractControl;

	nameOrt: AbstractControl;

	kuerzelOrt: AbstractControl;

	nameLand: AbstractControl;

	kuerzelLand: AbstractControl;

	emailAuftraggeber: AbstractControl;

	submitDisabled: boolean;

	headlineText: string;

	submited: boolean = false;

	private schuleEditorModel: SchuleEditorModel;

	private editSchuleInputSubscription: Subscription;

	constructor(private fb: FormBuilder, private katalogFacade: KatalogpflegeFacade) { }

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
			this.editSchuleForm.get('kuerzelLand').disable();
		}
		if (this.schuleEditorModel.nameLandDisabled) {
			this.editSchuleForm.get('nameLand').disable();
		}
		if (this.schuleEditorModel.nameOrtDisabled) {
			this.editSchuleForm.get('nameOrt').disable();
		}
	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submited = true;

		const schulePayload: SchulePayload = {
			name: this.name.value.trim(),
			kuerzel: this.kuerzel.value,
			nameOrt: this.nameOrt.value.trim(),
			kuerzelOrt: this.kuerzelOrt.value,
			nameLand: this.nameLand.value.trim(),
			kuerzelLand: this.kuerzelLand.value,
			emailAuftraggeber: this.emailAuftraggeber.value !== '' ? this.emailAuftraggeber.value.trim() : null
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
