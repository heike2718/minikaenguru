import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, AbstractControl, Validators } from '@angular/forms';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { Subscription } from 'rxjs';
import { initialLandPayload, LandPayload } from '../../katalogpflege.model';

@Component({
	selector: 'mka-edit-land',
	templateUrl: './edit-land.component.html',
	styleUrls: ['./edit-land.component.css']
})
export class EditLandComponent implements OnInit, OnDestroy {

	edtitLandInput$ = this.katalogFacade.editLandInput$;

	editLandForm!: FormGroup;

	name!: AbstractControl;

	kuerzel!: AbstractControl;

	submitDisabled: boolean = false;

	submited: boolean = false;

	private editLandInputSubscription: Subscription = new Subscription();

	private landPayload: LandPayload = initialLandPayload;

	constructor(private fb: FormBuilder, private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {

		this.submitDisabled = false;

		this.initForm();

		this.editLandInputSubscription = this.edtitLandInput$.subscribe(
			input => {
				if (input) {
					this.landPayload = input;					
				} else {
					this.landPayload = initialLandPayload;					
				}
				this.setFormValues();
			}
		);
	}

	ngOnDestroy(): void {
		this.editLandInputSubscription.unsubscribe();
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

		if (this.landPayload.kuerzel.length === 0) {
			return;
		}

		if (this.editLandForm) {
			this.editLandForm.setValue(this.landPayload);
		}

	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submited = true;

		const landPayload: LandPayload = {
			name: this.name? this.name.value.trim() : '',
			kuerzel: this.kuerzel ? this.kuerzel.value : ''
		};

		if (this.landPayload.kuerzel.length > 0) {
			this.katalogFacade.sendRenameLand(landPayload);
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
