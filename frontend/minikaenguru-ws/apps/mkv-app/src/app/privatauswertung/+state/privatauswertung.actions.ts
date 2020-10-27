import { createAction, props } from '@ngrx/store';
import { Kind } from '@minikaenguru-ws/common-components';


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

