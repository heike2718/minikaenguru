import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import * as NewsletterActions from './+state/newsletter.actions';
import * as NewsletterSelectors from './+state/newsletter.selectors';
import { Observable, timer, Subject } from 'rxjs';
import { Newsletter, Empfaengertyp, NewsletterVersandauftrag, Versandinfo, VersandinfoMap } from './newsletter.model';
import { NewsletterService } from './newsletter.service';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Router } from '@angular/router';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { switchMap, retry, share, takeUntil } from 'rxjs/operators';



@Injectable({
	providedIn: 'root'
})
export class NewsletterFacade {


	public newsletterEditorModel$: Observable<Newsletter> = this.store.select(NewsletterSelectors.newsletterEditorModel);
	public loading$: Observable<boolean> = this.store.select(NewsletterSelectors.loading);
	public newsletters$: Observable<Newsletter[]> = this.store.select(NewsletterSelectors.newsletters);
	public newslettersLoaded$: Observable<boolean> = this.store.select(NewsletterSelectors.newslettersLoaded);
	public selectedNewsletter$: Observable<Newsletter> = this.store.select(NewsletterSelectors.selectedNewsletter);
	public versandinfo$: Observable<Versandinfo> = this.store.select(NewsletterSelectors.versandinfo);

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

		this.store.dispatch(NewsletterActions.edidCanceled());

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

	public scheduleMailversand(auftrag: NewsletterVersandauftrag): void {

		this.store.dispatch(NewsletterActions.startBackendCall());

		this.newsletterService.scheduleMailversand(auftrag).subscribe(

			versandinfo => {
				this.store.dispatch(NewsletterActions.mailversandScheduled({ versandinfo: versandinfo }));
				this.messageService.info('Mailversand angeleiert');
				this.startPollVersandinfo(versandinfo);
			},
			(error => {
				this.store.dispatch(NewsletterActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})

		);
	}

	public resetState(): void {
		this.store.dispatch(NewsletterActions.resetNewsletters());
	}


	public startPollVersandinfo(versandinfo: Versandinfo): void {

		const optVersandinfo: Observable<Versandinfo> = timer(1, 10000).pipe(
			switchMap(() => this.newsletterService.getStatusNewsletterversand(versandinfo)),
			retry(),
			share(),
			takeUntil(this.stopPolling)
		);

		optVersandinfo.subscribe(
			info => {
				this.store.dispatch(NewsletterActions.versandinfoAktualisiert({ versandinfo: info }));

				if (!info) {
					this.propagateVersandBeendet(undefined);
				} else {
					if (info.versandBeendetAm) {
						this.propagateVersandBeendet(info.versandBeendetAm);
					}
				}
			},
			(error => {
				this.stopPollVersandinfo();
				this.errorHandler.handleError(error);
			})
		);
	}

	private propagateVersandBeendet(am: string): void {

		this.stopPollVersandinfo();

		if (am) {
			this.messageService.info('Mailversand beendet ' + am);
		} else {
			this.messageService.info('Mailversand beendet');
		}

		this.store.dispatch(NewsletterActions.versandBeendet());
	}

	public stopPollVersandinfo(): void {

		this.stopPolling.next();
	}
};
