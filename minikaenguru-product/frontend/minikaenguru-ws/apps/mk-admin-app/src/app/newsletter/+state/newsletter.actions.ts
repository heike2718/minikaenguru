import { createAction, props } from '@ngrx/store';
import { Newsletter, Versandauftrag } from '../../shared/newsletter-versandauftrage.model';


export const createNewNewsletter = createAction(
	'[NewsletterFacade] createNewNewsletter'
);

export const newsletterFromMustertextCreated = createAction(
	'[NewsletterFacade] createNewsletterFromMustertext',
	props<{newsletter: Newsletter}>()
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

export const editCanceled = createAction(
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
	props<{versandinfo: Versandauftrag}>()
);

export const versandinfoAktualisiert = createAction(
	'[NewsletterFacade]: pollVersandinfo - running',
	props<{versandinfo: Versandauftrag}>()
);

export const versandBeendet = createAction(
	'[NewsletterFacade]: pollVersandinfo - done'
);

export const newsletterRemoved = createAction(
	'[NewsletterFacade]: deleteNewsletter',
	props<{newsletter: Newsletter}>()
);

export const resetNewsletters = createAction(
	'[NewsletterFacade] resetState'
);


