import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, AbstractControl, Validators } from '@angular/forms';
import { InternalFacade } from '../application-services/internal.facade';
import { SchulkatalogAntrag } from '../domain/entities';
import { emailValidator, landValidator } from '@minikaenguru-ws/common-components';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { SchulkatalogConfigService, SchulkatalogConfig } from '../configuration/schulkatalog-config';

@Component({
	selector: 'mk-katalog-schulkatalog-antrag',
	templateUrl: './schulkatalog-antrag.component.html',
	styleUrls: ['./schulkatalog-antrag.component.css']
})
export class SchulkatalogAntragComponent implements OnInit, OnDestroy {


	antragForm!: UntypedFormGroup;

	email!: AbstractControl;

	schulname!: AbstractControl;

	ort!: AbstractControl;

	plz!: AbstractControl;

	strasseUndHausnummer!: AbstractControl;

	land!: AbstractControl;

	kleber!: AbstractControl;

	submitDisabled: boolean = false;

	showInfoLand: boolean = false;

	private submitted: boolean = false;

	private katalogantragSuccessSubscription: Subscription = new Subscription();

	constructor(@Inject(SchulkatalogConfigService) private config: SchulkatalogConfig, private fb: UntypedFormBuilder, private internalFacade: InternalFacade, private router: Router) { }

	ngOnInit(): void {

		this.katalogantragSuccessSubscription = this.internalFacade.katalogAntragSuccess$.subscribe(

			success => {
				if (this.submitted && success) {
					this.antragForm.reset();
				}
			}
		);

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
		this.katalogantragSuccessSubscription.unsubscribe();
	}

	toggleInfoLand() {
		this.showInfoLand = !this.showInfoLand;
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
		this.router.navigateByUrl(this.config.cancelKatalogantragRedirectPath);

	}

}
