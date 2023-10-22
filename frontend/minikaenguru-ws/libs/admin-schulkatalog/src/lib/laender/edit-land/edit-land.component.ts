import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { UntypedFormGroup, AbstractControl, UntypedFormBuilder, Validators, FormGroup, Form, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { emailValidator } from '@minikaenguru-ws/common-components';
import { AdminSchulkatalogConfigService } from '../../configuration/schulkatalog-config';
import { LandPayload, SchuleEditorModel, SchulePayload, initialSchuleEditorModel } from '../../admin-katalog.model';
import { AdminSchulkatalogFacade } from '../../admin-schulkatalog.facade';

@Component({
	selector: 'mka-edit-land',
	templateUrl: './edit-land.component.html',
	styleUrls: ['./edit-land.component.css']
})
export class EditLandComponent implements OnInit, OnDestroy {

	#config = inject(AdminSchulkatalogConfigService);
	#fb = inject(UntypedFormBuilder);
	#katalogFacade = inject(AdminSchulkatalogFacade);

	landPayload$ = this.#katalogFacade.landPayload$;

	devMode = this.#config.devmode;

	editLandForm!: UntypedFormGroup;

	nameLand!: AbstractControl;

	kuerzelLand!: AbstractControl;

	submitDisabled: boolean = false;

	submited: boolean = false;

	#landPayload: LandPayload = {kuerzel: '', name: ''};
	#editLandInputSubscription: Subscription = new Subscription();

	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.#editLandInputSubscription = this.landPayload$.subscribe(
			input => {
				this.#landPayload = input;
				this.setFormValues();
			}
		);
	}

	ngOnDestroy(): void {
		this.#editLandInputSubscription.unsubscribe();
	}

	private initForm() {

		this.editLandForm = this.#fb.group({
			'nameLand': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzelLand': this.#fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(10)] })
		});

		this.kuerzelLand = this.editLandForm.controls['kuerzelLand'];
		this.nameLand = this.editLandForm.controls['nameLand'];

	}

	private setFormValues() {

		this.editLandForm.reset();

		this.editLandForm.controls['kuerzelLand'].setValue(this.#landPayload.kuerzel);
		this.editLandForm.controls['nameLand'].setValue(this.#landPayload.name);
	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submited = true;

		const theLandPayload: LandPayload = {
			kuerzel: this.kuerzelLand ? this.kuerzelLand.value : '',
			name: this.nameLand ? this.nameLand.value.trim() : ''
		}

		this.#katalogFacade.updateLand(theLandPayload);
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
