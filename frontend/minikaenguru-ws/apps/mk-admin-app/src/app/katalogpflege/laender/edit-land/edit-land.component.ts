import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, AbstractControl, Validators } from '@angular/forms';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { Subscription } from 'rxjs';
import { LandPayload } from '../../katalogpflege.model';

@Component({
	selector: 'mka-edit-land',
	templateUrl: './edit-land.component.html',
	styleUrls: ['./edit-land.component.css']
})
export class EditLandComponent implements OnInit, OnDestroy {

	edtitLandInput$ = this.katalogFacade.editLandInput$;

	editLandForm: FormGroup;

	name: AbstractControl;

	kuerzel: AbstractControl;

	submitDisabled: boolean;

	submited: boolean;

	private editLandInputSubscription: Subscription;

	private landPayload: LandPayload;

	constructor(private fb: FormBuilder, private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.editLandInputSubscription = this.edtitLandInput$.subscribe(
			input => {
				this.landPayload = input;
				this.setFormValues();
			}
		);
	}

	ngOnDestroy(): void {
		if (this.editLandInputSubscription) {
			this.editLandInputSubscription.unsubscribe();
		}
	}

	private initForm() {

		this.editLandForm = this.fb.group({
			'name': this.fb.control({ value: '' }, { validators: [Validators.required, Validators.maxLength(100)] }),
			'kuerzel': this.fb.control({ value: '', disabled: true }),
		});

		this.name = this.editLandForm.controls['name'];
		this.kuerzel = this.editLandForm.controls['kuerzel'];
	}


	private setFormValues() {


		if (this.landPayload === undefined) {
			return;
		}

		this.editLandForm.setValue(this.landPayload);

	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submited = true;

		const landPayload: LandPayload = {
			name: this.name.value.trim(),
			kuerzel: this.kuerzel.value
		};

		this.katalogFacade.sendRenameLand(landPayload);
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
