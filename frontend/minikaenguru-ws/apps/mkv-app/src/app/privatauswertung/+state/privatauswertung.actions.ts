import { createAction, props } from '@ngrx/store';
import { Kind, Duplikatwarnung } from '@minikaenguru-ws/common-components';
import { Message } from '@minikaenguru-ws/common-messages';

export const teilnahmenummerInitialized = createAction(
	'[PrivatauswertungFacade] on loadKinder',
	props<{teilnahmenummer: string}>()
);

export const startLoading = createAction(
	'[PrivatauswertungFacade] before backendCall'
);

export const finishedWithError = createAction(
	'[PrivatauswertungFacade] on loadKinder error'
);

export const allKinderLoaded = createAction(
	'[PrivatauswertungFacade] on loadKinder finished with success',
	props<{kinder: Kind[]}>()
);

export const createNewKind = createAction(
	'[PrivatauswertungFacade] createNewKind'
);


export const startEditingKind = createAction(
	'[PrivatauswertungFacade] editKind',
	props<{ kind: Kind }>()
);

export const kindSaved = createAction(
	'[PrivatauswertungFacade] insert or update kind',
	props<{ kind: Kind, outcome: Message }>()
);

export const duplikatGeprueft = createAction(
	'[PrivatauswertungFacade] pruefeDuplikat',
	props<{ duplikatwarnung: Duplikatwarnung }>()

);

export const editCancelled = createAction(
	'[PrivatauswertungFacade] cancelEditKind'
);

export const kindDeleted = createAction(
	'[PrivatauswertungFacade] deleteKind',
	props<{ kind: Kind, outcome: Message }>()
);

