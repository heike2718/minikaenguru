import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { UntypedFormGroup, AbstractControl, UntypedFormBuilder, Validators, FormGroup, Form, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { emailValidator } from '@minikaenguru-ws/common-components';
import { AdminSchulkatalogConfigService } from '../../configuration/schulkatalog-config';
import { SchuleEditorModel, SchulePayload, initialSchuleEditorModel } from '../../admin-katalog.model';
import { AdminSchulkatalogFacade } from '../../admin-schulkatalog.facade';

@Component({
	selector: 'mka-edit-schule',
	templateUrl: './edit-schule.component.html',
	styleUrls: ['./edit-schule.component.css']
})
export class EditSchuleComponent implements OnInit, OnDestroy {

	#config = inject(AdminSchulkatalogConfigService);
	#fb = inject(UntypedFormBuilder);
	#katalogFacade = inject(AdminSchulkatalogFacade);

	devMode = this.#config.devmode;

	editSchuleInput$ = this.#katalogFacade.schuleEditorModel$;

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

	#schuleEditorModel: SchuleEditorModel = initialSchuleEditorModel;
	#editSchuleInputSubscription: Subscription = new Subscription();



	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.#editSchuleInputSubscription = this.editSchuleInput$.subscribe(
			input => {
				this.#schuleEditorModel = input;
				this.setFormValues();
			}
		);
	}

	ngOnDestroy(): void {
		this.#editSchuleInputSubscription.unsubscribe();
	}

	private initForm() {

		this.editSchuleForm = this.#fb.group({
			'name': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzel': this.#fb.control({ value: '', disabled: true }),
			'nameOrt': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzelOrt': this.#fb.control({ value: '', disabled: true }),
			'nameLand': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzelLand': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(10)] }),
			'emailAuftraggeber': this.#fb.control({ value: '' }, { validators: [emailValidator] }),
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

		if (this.#schuleEditorModel.schulePayload === undefined) {
			return;
		}

		this.editSchuleForm.reset();

		this.headlineText = this.#schuleEditorModel.modusCreate ? 'Schule anlegen' : 'Schule ändern';
		this.editSchuleForm.setValue(this.#schuleEditorModel.schulePayload);

		if (this.#schuleEditorModel.kuerzelLandDisabled) {
			const input = this.editSchuleForm.get('kuerzelLand');
			if (input) {
				input.disable();
			}
		}
		if (this.editSchuleForm && this.#schuleEditorModel.nameLandDisabled) {
			const input = this.editSchuleForm.get('nameLand');
			if (input) {
				input.disable();
			}
		}
		if (this.editSchuleForm && this.#schuleEditorModel.nameOrtDisabled) {
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

		if (this.#schuleEditorModel.modusCreate) {
			this.#katalogFacade.createSchule(schulePayload);
		} else {
			this.#katalogFacade.renameSchule(schulePayload);
		}
	}

	cancel(): void {

		this.setFormValues();
		this.submitDisabled = false;
		this.submited = false;

	}

	gotoKataloge(): void {

	}
}
