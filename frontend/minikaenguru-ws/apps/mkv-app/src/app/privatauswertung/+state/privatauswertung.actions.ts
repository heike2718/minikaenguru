import { createAction, props } from '@ngrx/store';
import { Kind, Duplikatwarnung } from '@minikaenguru-ws/common-components';
import { Message } from '@minikaenguru-ws/common-messages';


export const startLoading = createAction(
	'[PrivatauswertungFacade] before load privatkinder'
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
	'[PrivatauswertungFacade] saveKind',
	props<{ kind: Kind, outcome: Message }>()
);

export const duplikatGeprueft = createAction(
	'[PrivatauswertungFacade] pruefeDuplikat',
	props<{ duplikatwarnung: Duplikatwarnung }>()

);

