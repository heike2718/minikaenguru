import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { UntypedFormGroup, AbstractControl, UntypedFormBuilder, Validators, FormGroup, Form, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AdminSchulkatalogConfigService } from '../../configuration/schulkatalog-config';
import { OrtPayload } from '../../admin-katalog.model';
import { AdminSchulkatalogFacade } from '../../admin-schulkatalog.facade';

@Component({
	selector: 'mka-edit-ort',
	templateUrl: './edit-ort.component.html',
	styleUrls: ['./edit-ort.component.css']
})
export class EditOrtComponent implements OnInit, OnDestroy {

	#config = inject(AdminSchulkatalogConfigService);
	#fb = inject(UntypedFormBuilder);
	#katalogFacade = inject(AdminSchulkatalogFacade);

	ortPayload$ = this.#katalogFacade.ortPayload$;

	devMode = this.#config.devmode;

	editOrtForm!: UntypedFormGroup;

	nameOrt!: AbstractControl;

	kuerzelOrt!: AbstractControl;

	nameLand!: AbstractControl;

	kuerzelLand!: AbstractControl;

	submitDisabled: boolean = false;

	submited: boolean = false;

	#ortPayload: OrtPayload = {kuerzel: '', name: '', kuerzelLand: '', nameLand: ''};
	#editOrtInputSubscription: Subscription = new Subscription();

	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.#editOrtInputSubscription = this.ortPayload$.subscribe(
			input => {
				this.#ortPayload = input;
				this.setFormValues();
			}
		);
	}

	ngOnDestroy(): void {
		this.#editOrtInputSubscription.unsubscribe();
	}

	private initForm() {

		this.editOrtForm = this.#fb.group({
			'nameOrt': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzelOrt': this.#fb.control({ value: '', disabled: true }),
			'nameLand': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzelLand': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(10)] })
		});

		this.kuerzelOrt = this.editOrtForm.controls['kuerzelOrt'];
		this.nameOrt = this.editOrtForm.controls['nameOrt'];
		this.kuerzelLand = this.editOrtForm.controls['kuerzelLand'];
		this.nameLand = this.editOrtForm.controls['nameLand'];

		this.kuerzelOrt.disable();
		this.kuerzelLand.disable();
		this.nameLand.disable();

	}

	private setFormValues() {

		this.editOrtForm.reset();

		this.editOrtForm.controls['kuerzelOrt'].setValue(this.#ortPayload.kuerzel);
		this.editOrtForm.controls['nameOrt'].setValue(this.#ortPayload.name);
		this.editOrtForm.controls['kuerzelLand'].setValue(this.#ortPayload.kuerzelLand);
		this.editOrtForm.controls['nameLand'].setValue(this.#ortPayload.nameLand);
	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submited = true;

		const theOrtPayload: OrtPayload = {
			kuerzel: this.kuerzelOrt ? this.kuerzelOrt.value : '',
			name: this.nameOrt ? this.nameOrt.value.trim() : '',
			kuerzelLand: this.kuerzelLand ? this.kuerzelLand.value : '',
			nameLand: this.nameLand ? this.nameLand.value.trim() : ''
		}

		this.#katalogFacade.updateOrt(theOrtPayload);
	}

	cancel(): void {

		this.setFormValues();
		this.submitDisabled = false;
		this.submited = false;

	}

	gotoSchulkatalog(): void {
		this.#katalogFacade.navigateToSchulkatalog();
	}
}
