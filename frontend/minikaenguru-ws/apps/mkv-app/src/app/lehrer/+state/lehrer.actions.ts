import { createAction, props } from '@ngrx/store';
import { Schule } from './../schulen/schulen.model';
import { Schulteilnahme, Lehrer } from '../../wettbewerb/wettbewerb.model';
import { KatalogItem } from '@minikaenguru-ws/common-schulkatalog';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';


export const datenLehrerGeladen = createAction(
	'[LehrerFacade] loadLehrer',
	props<{lehrer: Lehrer}>()
);

export const schulenLoaded = createAction(
	'[LehrerFacade] loadSchulen',
	props<{ schulen: Schule[] }>()
);

export const finishedWithError = createAction(
	'[LehrerFacade] on error'
);

export const schuleDetailsLoaded = createAction(
	'[LehrerFacade] loadDetails subscription',
	props<{ schule: Schule }>()
);

export const vertragAdvCreated= createAction(
	'[VertragAdvFacade] submitVertrag success',
	props<{schulkuerzel: string}>()
);

export const restoreDetailsFromCache = createAction(
	'[LehrerFacade] restoreDetailsFromCache',
	props<{ kuerzel: string }>()
);

export const selectSchule = createAction(
	'[LehrerFacade] selectSchule',
	props<{ schule: Schule }>()
);

export const deselectSchule = createAction(
	'[LehrerFacade/KlassenFacade] resetSelection/resetState'
);

export const schuleAngemeldet = createAction(
	'[LehrerFacade] schuleAnmelden',
	props<{ teilnahme: Schulteilnahme, angemeldetDurch: string}>()
);

export const schulkatalogEinblenden = createAction(
	'[LehrerFacade] schulkatalogEinblenden'
);

export const neueSchuleSelected = createAction(
	'[LehrerFacade] neueSchuleSelected',
	props<{selectedKatalogItem: KatalogItem}>()
);

export const schuleRemoved = createAction(
	'[LehrerFacade] schuleEntfernen',
	props<{ kuerzel: string }>()
);

export const schuleAdded = createAction(
	'[LehrerFacade] schuleHinzufuegen',
	props<{ schule: Schule }>()
);

export const setShowTextSchuleBereitsZugeordnet = createAction(
	'[LehrerFacade] setShowTextSchuleBereitsZugeordnet',
	props<{value: boolean}>()
);

export const closeSchulsuche = createAction(
	'[LehrerFacade] closeSchulsuche'
);

export const aboNewsletterChanged = createAction(
	'[LehrerFacade] changeAboNewsletter'
);

export const auswertungstabelleHochgeladen = createAction(
	'[LehrerFacade] handleAuswertungstabelleHochgeladen',
	props<{schule: Schule}>()
)

export const resetLehrer = createAction(
	'[LehrerFacade] resetState()'
);

