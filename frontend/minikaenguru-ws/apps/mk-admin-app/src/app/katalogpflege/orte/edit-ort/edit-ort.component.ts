import { Component, OnInit, OnDestroy } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { OrtPayload } from '../../katalogpflege.model';
import { environment } from '../../../../environments/environment';

@Component({
	selector: 'mka-edit-ort',
	templateUrl: './edit-ort.component.html',
	styleUrls: ['./edit-ort.component.css']
})
export class EditOrtComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	editOrtInput$ = this.katalogFacade.editOrtInput$;

	editOrtForm: FormGroup;

	name: AbstractControl;

	kuerzel: AbstractControl;

	nameLand: AbstractControl;

	kuerzelLand: AbstractControl;

	submitDisabled: boolean;

	submited: boolean;

	private editOrtInputSubscription: Subscription;

	private ortPayload: OrtPayload;

	constructor(private fb: FormBuilder, private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.editOrtInputSubscription = this.editOrtInput$.subscribe(
			input => {
				this.ortPayload = input;
				this.setFormValues();
			}
		);
	}

	ngOnDestroy(): void {
		if (this.editOrtInputSubscription) {
			this.editOrtInputSubscription.unsubscribe();
		}
	}

	private initForm() {

		this.editOrtForm = this.fb.group({
			'name': this.fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzel': this.fb.control({ value: '', disabled: true }),
			'nameLand': this.fb.control({ value: '', disabled: true }),
			'kuerzelLand': this.fb.control({ value: '', disabled: true })
		});

		this.name = this.editOrtForm.controls['name'];
		this.kuerzel = this.editOrtForm.controls['kuerzel'];
		this.kuerzelLand = this.editOrtForm.controls['kuerzelLand'];
		this.nameLand = this.editOrtForm.controls['nameLand'];
	}


	private setFormValues() {


		if (this.ortPayload === undefined) {
			return;
		}

		this.editOrtForm.setValue(this.ortPayload);

	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submited = true;

		const ortPayload: OrtPayload = {
			name: this.name.value.trim(),
			kuerzel: this.kuerzel.value,
			nameLand: this.nameLand.value,
			kuerzelLand: this.kuerzelLand.value
		};

		this.katalogFacade.sendRenameOrt(ortPayload);
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
