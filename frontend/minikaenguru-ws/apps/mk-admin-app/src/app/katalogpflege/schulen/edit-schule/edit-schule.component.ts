import { Component, OnInit, OnDestroy } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { SchulePayload } from '../../katalogpflege.model';
import { Subscription } from 'rxjs';

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

	submitDisabled: boolean;

	headlineText: string;

	submited: boolean = false;

	private initialPayload: SchulePayload;

	private modusCreate: boolean;

	private editSchuleInputSubscription: Subscription;

	constructor(private fb: FormBuilder, private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.editSchuleInputSubscription = this.editSchuleInput$.subscribe(
			input => {
				if (input.payload !== undefined) {
					this.initialPayload = input.payload;
					this.modusCreate = input.isModusCreate;
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
		});

		this.name = this.editSchuleForm.controls['name'];
		this.kuerzel = this.editSchuleForm.controls['kuerzel'];
		this.nameOrt = this.editSchuleForm.controls['nameOrt'];
		this.kuerzelOrt = this.editSchuleForm.controls['kuerzelOrt'];
		this.nameLand = this.editSchuleForm.controls['nameLand'];
		this.kuerzelLand = this.editSchuleForm.controls['kuerzelLand'];

	}

	private setFormValues() {

		this.headlineText = this.modusCreate ? 'Schule anlegen' : 'Schule ändern';
		this.editSchuleForm.setValue(this.initialPayload);

		if (!this.modusCreate) {
			this.editSchuleForm.get('nameOrt').disable();
			this.editSchuleForm.get('nameLand').disable();
			this.editSchuleForm.get('kuerzelLand').disable();
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
			kuerzelLand: this.kuerzelLand.value
		};

		// TODO:
	}

	cancel(): void {

		this.setFormValues();
		this.submitDisabled = false;
		this.submited = false;

	}

	gotoKataloge(): void {
		this.katalogFacade.finishEditSchule();
	}


}
