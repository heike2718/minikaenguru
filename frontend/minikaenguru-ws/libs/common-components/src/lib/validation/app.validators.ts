import { AbstractControl, FormGroup, FormControl } from '@angular/forms';


export function emailValidator(control: AbstractControl): {
	[key: string]: any} {

	if (!control.value || control.value.trim() === '') {
		return null;
	} else {

		const re = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
		const value = control.value.trim();

		if (re.test(value)) {
			return null;
		} else {
			return { 'invalidEMail': true };
		}
	}
};

export function landValidator(control: AbstractControl): {
	[key: string]: any
} {
	if (!control.value || control.value.trim() === '') {
		return null;
	} else {

		const value = control.value.trim().toLowerCase();

		if (value.includes('deutsch') || value.includes('bundesrepublik') || value === 'brd') {
			return { 'invalidLandDeutschland': true };
		}
		return null;

	}
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
