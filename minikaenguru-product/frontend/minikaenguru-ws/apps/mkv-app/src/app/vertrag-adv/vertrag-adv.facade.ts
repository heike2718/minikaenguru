import { Injectable } from '@angular/core';
import * as vertragAdvSelectors from './+state/vertrag-adv.selectors';
import * as VertragAdvActions from './+state/vertrag-adv.actions';
import * as LehrerActions from '../lehrer/+state/lehrer.actions';
import { AppState } from '../reducers';
import { Store } from '@ngrx/store';
import { VertragAdvEditorModel } from './vertrag-adv.model';
import { VertrtagAdvService } from './vertrtag-adv.service';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { take } from 'rxjs/operators';
import { Schule } from '../lehrer/schulen/schulen.model';
import { UntypedFormGroup } from '@angular/forms';

@Injectable({
	providedIn: 'root'
})
export class VertragAdvFacade {

	public selectedSchule$ = this.store.select(vertragAdvSelectors.selectedSchule);
	public vertragAdvEditorModel$ = this.store.select(vertragAdvSelectors.vertragAdvEditorModel);
	public submitDisabled$ = this.store.select(vertragAdvSelectors.submitDisabled);
	public saveInProgress$ = this.store.select(vertragAdvSelectors.saveInProgress);

	constructor(private store: Store<AppState>,
		private vertragAdvService: VertrtagAdvService,
		private errorHandler: GlobalErrorHandlerService,
		private messageService: MessageService,

	) { }

	public setSelectedSchule(schule: Schule) {
		this.store.dispatch(VertragAdvActions.selectSchule({ schule: schule }));
	}

	public initEditorModel(schule: Schule) {

		const editorModel: VertragAdvEditorModel = {
			schulkuerzel: schule.kuerzel,
			hausnummer: '',
			ort: schule.ort,
			plz: '',
			schulname: schule.name,
			strasse: ''
		};

		this.store.dispatch(VertragAdvActions.editorModelInitialized({ model: editorModel }));
	}


	public validateForm(form: UntypedFormGroup): void {

		const valid = form.valid;
		this.store.dispatch(VertragAdvActions.formValidated({ valid: valid }));
	}

	public submitVertrag(vertrag: VertragAdvEditorModel): void {

		this.store.dispatch(VertragAdvActions.submitStarted());

		this.vertragAdvService.submitVertrag(vertrag).pipe(
			take(1)
		).subscribe(
			message => {
				this.store.dispatch(LehrerActions.vertragAdvCreated({schulkuerzel: vertrag.schulkuerzel}));
				this.store.dispatch(VertragAdvActions.submitFinished());
				this.messageService.showMessage(message);
			},
			(error) => {
				this.store.dispatch(VertragAdvActions.submitFinished());
				this.errorHandler.handleError(error);
			}
		);
	}

	public resetState() {
		this.store.dispatch(VertragAdvActions.editVertragFinished());
	}
}
