import { createAction, props } from '@ngrx/store';
import { Newsletter, Versandinfo } from '../newsletter.model';
import { Message } from '@angular/compiler/src/i18n/i18n_ast';


export const createNewNewsletter = createAction(
	'[NewsletterFacade] createNewNewsletter'
);

export const startBackendCall = createAction(
	'[NewsletterFacade] diverse'
);

export const backendCallFinishedWithError = createAction(
	'[NewsletterFacade] errors'
);

export const newslettersLoaded = createAction(
	'[NewsletterFacade]: loadNewsletters',
	props<{newsletters: Newsletter[]}>()
);

export const edidCanceled = createAction(
	'[NewsletterFacade]: cancelEdit'
);

export const editNewsletterTriggered = createAction(
	'[NewsletterFacade]: startEditNewsletter',
	props<{newsletter: Newsletter}>()
);


export const newsletterSaved = createAction(
	'[NewsletterFacade]: insertNewsletter / updateNewsletter',
	props<{newsletter: Newsletter}>()
);

export const mailversandScheduled = createAction(
	'[NewsletterFacade]: scheduleMailversand',
	props<{versandinfo: Versandinfo}>()
);

export const versandinfoAktualisiert = createAction(
	'[NewsletterFacade]: pollVersandinfo',
	props<{versandinfo: Versandinfo}>()
);

export const newsletterRemoved = createAction(
	'[NewsletterFacade]: deleteNewsletter',
	props<{newsletter: Newsletter}>()
);

export const resetNewsletters = createAction(
	'[NewsletterFacade] resetState'
);


