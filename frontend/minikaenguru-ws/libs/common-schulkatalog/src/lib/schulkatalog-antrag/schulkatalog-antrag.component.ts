import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, AbstractControl, Validators } from '@angular/forms';
import { InternalFacade } from '../application-services/internal.facade';
import { SchulkatalogAntrag } from '../domain/entities';
import { emailValidator, landValidator } from '@minikaenguru-ws/common-components';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mk-katalog-schulkatalog-antrag',
	templateUrl: './schulkatalog-antrag.component.html',
	styleUrls: ['./schulkatalog-antrag.component.css']
})
export class SchulkatalogAntragComponent implements OnInit, OnDestroy {


	antragForm: FormGroup;

	email: AbstractControl;

	schulname: AbstractControl;

	ort: AbstractControl;

	plz: AbstractControl;

	strasseUndHausnummer: AbstractControl;

	land: AbstractControl;

	kleber: AbstractControl;

	submitDisabled: boolean;

	private submitted: boolean = false;

	private katalogantragSuccessSubscription: Subscription;

	constructor(private fb: FormBuilder, private internalFacade: InternalFacade) { }

	ngOnInit(): void {



		this.katalogantragSuccessSubscription = this.internalFacade.katalogAntragSuccess$.subscribe(

			success => {
				if (this.submitted && success) {
					this.antragForm.reset();
				}
			}

		)

		this.submitDisabled = false;

		this.antragForm = this.fb.group({
			'email': this.fb.control('', {validators: [Validators.required, emailValidator]}),
			'land': this.fb.control('', {validators: [Validators.required, landValidator, Validators.maxLength(100)]}),
			'ort': this.fb.control('', {validators: [Validators.required, Validators.maxLength(100)]}),
			'plz': this.fb.control('', {validators: [Validators.maxLength(20)]}),
			'schulname': this.fb.control('', {validators: [Validators.required, Validators.maxLength(100)]}),
			'strasseUndHausnummer': this.fb.control('', {validators: [Validators.maxLength(200)]}),
			'kleber': this.fb.control('')
		});

		this.email = this.antragForm.controls['email'];
		this.schulname = this.antragForm.controls['schulname'];
		this.ort = this.antragForm.controls['ort'];
		this.plz = this.antragForm.controls['plz'];
		this.strasseUndHausnummer = this.antragForm.controls['strasseUndHausnummer'];
		this.land = this.antragForm.controls['land'];
		this.kleber = this.antragForm.controls['kleber'];
	}

	ngOnDestroy(): void {
		if (this.katalogantragSuccessSubscription) {
			this.katalogantragSuccessSubscription.unsubscribe();
		}
	}

	submitForm(): void {

		this.submitDisabled = true;
		this.submitted = true;

		const antrag: SchulkatalogAntrag = {
			email: this.email.value.trim(),
			schulname: this.schulname.value.trim(),
			ort: this.ort.value.trim(),
			plz: this.plz.value.trim(),
			strasseUndHausnummer: this.strasseUndHausnummer.value.trim(),
			land: this.land.value.trim(),
			kleber: this.kleber.value
		};

		this.internalFacade.submitSchulkatalogAntrag(antrag);

		this.submitDisabled = false;

	}

	cancel(): void {

		this.antragForm.reset();
		this.submitDisabled = false;
		this.submitted = false;

	}

}
