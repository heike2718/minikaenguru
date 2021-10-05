import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../environments/environment';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { NewsletterFacade } from '../newsletter.facade';
import { Subscription } from 'rxjs';
import { initialNewsletter, Newsletter } from '../newsletter.model';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { Router } from '@angular/router';

@Component({
	selector: 'mka-edit-newsletter',
	templateUrl: './edit-newsletter.component.html',
	styleUrls: ['./edit-newsletter.component.css']
})
export class EditNewsletterComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	newsletterForm!: FormGroup;

	betreffControl!: FormControl;

	textControl!: FormControl;

	editorInitialized = false;

	saveInProgress = false;

	private uuid: string = '';

	private editorModelSubscription: Subscription = new Subscription();

	constructor(private fb: FormBuilder,
		private router: Router,
		public newsletterFacade: NewsletterFacade,
		private messageService: MessageService) {

			this.initForm();
		}

	ngOnInit(): void {

		this.editorModelSubscription = this.newsletterFacade.newsletterEditorModel$.subscribe(

			model => {
				if (model) {

					const betreff = this.newsletterForm.get('betreff');
					if (betreff) {
						betreff.setValue(model.betreff, { onlySelf: true });
					}
					const text = this.newsletterForm.get('text');
					if (text) {
						text.setValue(model.text, { onlySelf: true });
					}

					this.saveInProgress = false;
					this.editorInitialized = true;

					this.uuid = model.uuid;
				}
			}
		)
	}

	ngOnDestroy(): void {

		if (this.editorModelSubscription) {
			this.editorModelSubscription.unsubscribe();
		}
	}

	onSubmit(): void {

		this.saveInProgress = true;
		const formValue = this.newsletterForm.value;

		const daten: Newsletter = {
			betreff: formValue.betreff.trim(),
			text: formValue.text,
			uuid: this.uuid,
			versandinfoIDs: []
		};

		this.newsletterFacade.saveNewsletter(daten);
	}

	onCancel(): void {

		this.messageService.clear();
		this.newsletterFacade.cancelEditNewsletter();

		this.router.navigateByUrl('/newsletters');

	}

	private initForm(): void {

		this.betreffControl = new FormControl({value: ''}, { validators: [Validators.required, Validators.maxLength(100)] })
		this.textControl = new FormControl({value: ''}, { validators: [Validators.required, Validators.minLength(1)] })


		this.newsletterForm = this.fb.group({
			betreff: this.betreffControl,
			text: this.textControl
		});
	}

}
