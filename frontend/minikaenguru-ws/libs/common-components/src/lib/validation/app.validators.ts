import { AbstractControl, FormGroup, FormControl } from '@angular/forms';
import { Klassenstufe, getKlassenstufeByLabel } from '../common-components.model';


export function emailValidator(control: AbstractControl): {
	[key: string]: any
} {

	const theValue: string = extractTheValueAsString(control);

	if (theValue === '') {
		return null;
	}

	const re = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
	if (re.test(theValue)) {
		return null;
	} else {
		return { 'invalidEMail': true };
	}

};

export function landValidator(control: AbstractControl): {
	[key: string]: any
} {
	const value: string = extractTheValueAsString(control).toLowerCase();

	if (value.includes('deutsch') || value.includes('bundesrepublik') || value === 'brd') {
		return { 'invalidLandDeutschland': true };
	}
	return null;
};

export function validateAllFormFields(formGroup: FormGroup): void {
	Object.keys(formGroup.controls).forEach(field => {
		const control = formGroup.get(field);
		if (control instanceof FormControl) {
			control.markAsTouched({ onlySelf: true });
		} else if (control instanceof FormGroup) {
			this.validateAllFormFields(control);
		}
	});
};



// =============================  private functions =========================

function extractTheValueAsString(control: AbstractControl): string {


	if (control) {
		if (control.value) {
			if (control.value.value) {
				return control.value.value;
			} else {
				return control.value;
			}
		}
	}

	return '';
}
