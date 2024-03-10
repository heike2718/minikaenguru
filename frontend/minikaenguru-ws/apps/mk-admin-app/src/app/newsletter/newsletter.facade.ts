import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import * as NewsletterActions from './+state/newsletter.actions';
import * as NewsletterSelectors from './+state/newsletter.selectors';
import { Observable, timer, Subject } from 'rxjs';
import { Newsletter, NewsletterVersandauftrag, Versandauftrag, initialNewsletterEditorModel } from './../shared/newsletter-versandauftrage.model';
import { NewsletterService } from './newsletter.service';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Router } from '@angular/router';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { switchMap, retry, share, takeUntil } from 'rxjs/operators';
import { Mustertext } from '../shared/shared-entities.model';



@Injectable({
	providedIn: 'root'
})
export class NewsletterFacade {


	public newsletterEditorModel$: Observable<Newsletter | undefined> = this.store.select(NewsletterSelectors.newsletterEditorModel);
	public loading$: Observable<boolean> = this.store.select(NewsletterSelectors.loading);
	public newsletters$: Observable<Newsletter[]> = this.store.select(NewsletterSelectors.newsletters);
	public newslettersLoaded$: Observable<boolean> = this.store.select(NewsletterSelectors.newslettersLoaded);
	public selectedNewsletter$: Observable<Newsletter | undefined> = this.store.select(NewsletterSelectors.selectedNewsletter);
	public versandinfo$: Observable<Versandauftrag | undefined> = this.store.select(NewsletterSelectors.versandinfo);

	public empfaengertypen: string[] = ['', 'TEST', 'ALLE', 'LEHRER', 'PRIVATVERANSTALTER'];

	private stopPolling = new Subject();

	constructor(private store: Store<AppState>,
		private newsletterService: NewsletterService,
		private errorHandler: GlobalErrorHandlerService,
		private messageService: MessageService,
		private router: Router
	) { }

	public createNewNewsletter(): void {

		this.store.dispatch(NewsletterActions.createNewNewsletter());
		this.router.navigateByUrl('/newsletter-editor/neu');
	}

	public createNewsletterFromMustertext(mustertext: Mustertext): void {

		if (mustertext.text) {
			const betreff = initialNewsletterEditorModel.betreff + ' ' + mustertext.name;
			const newsletter = {...initialNewsletterEditorModel, betreff: betreff, text: mustertext.text};
			this.store.dispatch(NewsletterActions.newsletterFromMustertextCreated({newsletter: newsletter}));

			this.router.navigateByUrl('/newsletter-editor/neu');
		}
	}	

	public loadNewsletters(): void {

		this.store.dispatch(NewsletterActions.startBackendCall());

		this.newsletterService.loadNewsletters().subscribe(

			newsletters => {
				this.store.dispatch(NewsletterActions.newslettersLoaded({ newsletters: newsletters }));

			},
			(error => {
				this.store.dispatch(NewsletterActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})
		);

	}

	public startEditNewsletter(newsletter: Newsletter): void {

		this.store.dispatch(NewsletterActions.editNewsletterTriggered({ newsletter: newsletter }));
		this.router.navigateByUrl('/newsletter-editor/' + newsletter.uuid);
	}

	public cancelEditNewsletter(): void {

		this.store.dispatch(NewsletterActions.editCanceled());

	}

	public saveNewsletter(editorModel: Newsletter): void {

		this.store.dispatch(NewsletterActions.startBackendCall());

		if (editorModel.uuid === 'neu') {

			this.newsletterService.addNewsletter(editorModel).subscribe(

				newsletter => {
					this.store.dispatch(NewsletterActions.newsletterSaved({ newsletter: newsletter }));
					this.messageService.info('Newsletter gespeichert');
				},
				(error => {
					this.store.dispatch(NewsletterActions.backendCallFinishedWithError());
					this.errorHandler.handleError(error);
				})

			);
		} else {
			this.newsletterService.updateNewsletter(editorModel).subscribe(

				newsletter => {
					this.store.dispatch(NewsletterActions.newsletterSaved({ newsletter: newsletter }));
					this.messageService.info('Newsletter gespeichert');
				},
				(error => {
					this.store.dispatch(NewsletterActions.backendCallFinishedWithError());
					this.errorHandler.handleError(error);
				})

			);
		}

	}


	public deleteNewsletter(newsletter: Newsletter): void {

		this.store.dispatch(NewsletterActions.startBackendCall());

		this.newsletterService.deleteNewsletter(newsletter.uuid).subscribe(

			message => {
				this.store.dispatch(NewsletterActions.newsletterRemoved({ newsletter: newsletter }));
				this.messageService.showMessage(message);
			},
			(error => {
				this.store.dispatch(NewsletterActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})

		);
	}
};
