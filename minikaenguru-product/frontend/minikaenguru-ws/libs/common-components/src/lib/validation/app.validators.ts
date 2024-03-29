import { AbstractControl, UntypedFormGroup, UntypedFormControl } from '@angular/forms';


export function emailValidator(control: AbstractControl): {
	[key: string]: any
} | null {	

	const value: string = extractTheValueAsString(control);

	if (value === '') {
		return null;
	}

	const re = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
	if (re.test(value.toLowerCase())) {
		return null;
	} else {
		return { 'invalidEMail': true };
	}

};

export function landValidator(control: AbstractControl): {
	[key: string]: any
} | null {
	const value: string = extractTheValueAsString(control).toLowerCase();

	if (value === '') {
		return null;
	}

	if (value.includes('deutsch') || value.includes('bundesrepublik') || value.toUpperCase() === 'BRD' || value.toUpperCase() === 'DE') {
		return { 'invalidLandDeutschland': true };
	}
	return null;
};

export function validateAllFormFields(formGroup: UntypedFormGroup): void {
	Object.keys(formGroup.controls).forEach(field => {
		const control = formGroup.get(field);
		if (control instanceof UntypedFormControl) {
			control.markAsTouched({ onlySelf: true });
		} else if (control instanceof UntypedFormGroup) {
			validateAllFormFields(control);
		}
	});
};



export function extractTheValueAsString(control: AbstractControl): string {

	if (control) {

		if (!control.dirty) {
			return '';
		}

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
