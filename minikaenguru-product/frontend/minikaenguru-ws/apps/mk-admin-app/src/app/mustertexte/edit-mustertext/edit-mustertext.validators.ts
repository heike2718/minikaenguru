import { AbstractControl } from "@angular/forms";
import { extractTheValueAsString } from "@minikaenguru-ws/common-components";

export function mustertextKategoriValidator(control: AbstractControl): {
	[key: string]: any
} | null {

	const theValue: string = extractTheValueAsString(control);

	if (theValue === '') {
		return null;
	}

	if (theValue !== 'UNDEFINED') {
		return null;
	} else {
		return { 'invalidMustertextKategorie': true };
	}

};



