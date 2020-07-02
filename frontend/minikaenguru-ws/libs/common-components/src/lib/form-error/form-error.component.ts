import { Component, OnInit, Input, Optional } from '@angular/core';
import { FormGroup, FormGroupDirective, NgForm } from '@angular/forms';

@Component({
	selector: 'mk-form-error',
	templateUrl: './form-error.component.html',
	styleUrls: ['./form-error.component.css']
})
export class FormErrorComponent implements OnInit {

	@Input() path;
	@Input() text = '';


	constructor(@Optional() private ngForm: NgForm,
		@Optional() private formGroup: FormGroupDirective) { }

	ngOnInit() {
	}

	get errorMessages(): string[] {
		let form: FormGroup;

		if (this.ngForm) {
			form = this.ngForm.form;
		} else {
			form = this.formGroup.form;
		}
		const messages = [];
		const control = form.get(this.path);
		if (!control || !(control.touched) || !control.errors) {
			return null;
		}
		for (const code in control.errors) {
			if (control.errors.hasOwnProperty(code)) {
				const error = control.errors[code];
				let message = '';
				switch (code) {
					case 'required':
						message = `${this.text} ist ein Pflichtfeld.`;
						break;
					case 'minlength':
						message = `${this.text} muss mindestens ${error.requiredLength} Zeichen lang sein.`;
						break;
					case 'maxlength':
						message = `${this.text} darf maximal ${error.requiredLength} Zeichen lang sein.`;
						break;
					case 'invalidEMail':
						message = `Bitte geben Sie eine gültige E-Mail Adresse an.`;
						break;
					case 'invalidLandDeutschland':
						message = `Bitte geben Sie eins der 16 Bundesländer an.`;
						break;
				}

				messages.push(message);
			}
		}

		return messages;
	}
}
