import { Component, OnInit, OnDestroy } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { UntypedFormGroup, AbstractControl, UntypedFormBuilder, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { initialOrtPayload, OrtPayload } from '../../katalogpflege.model';
import { environment } from '../../../../environments/environment';

@Component({
	selector: 'mka-edit-ort',
	templateUrl: './edit-ort.component.html',
	styleUrls: ['./edit-ort.component.css']
})
export class EditOrtComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	editOrtInput$ = this.katalogFacade.editOrtInput$;

	editOrtForm!: UntypedFormGroup;

	name!: AbstractControl;

	kuerzel!: AbstractControl;

	nameLand!: AbstractControl;

	kuerzelLand!: AbstractControl;

	submitDisabled: boolean = false;

	submited: boolean = false;

	private editOrtInputSubscription: Subscription = new Subscription();

	private ortPayload: OrtPayload = initialOrtPayload;

	constructor(private fb: UntypedFormBuilder, private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.editOrtInputSubscription = this.editOrtInput$.subscribe(
			input => {
				if (input) {
					this.ortPayload = input;					
				} else {
					this.ortPayload = initialOrtPayload;
				}

				this.setFormValues();
			}
		);
	}

	ngOnDestroy(): void {
		this.editOrtInputSubscription.unsubscribe();
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


		if (this.ortPayload.kuerzel.length === 0) {
			return;
		}

		if (this.editOrtForm) {
			this.editOrtForm.setValue(this.ortPayload);
		}
	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submited = true;

		const ortPayload: OrtPayload = {
			name: this.name ? this.name.value.trim() : '',
			kuerzel: this.kuerzel ? this.kuerzel.value : '',
			nameLand: this.nameLand ? this.nameLand.value : '',
			kuerzelLand: this.kuerzelLand ? this.kuerzelLand.value : ''
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
