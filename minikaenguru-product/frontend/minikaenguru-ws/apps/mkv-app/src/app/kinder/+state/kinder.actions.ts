import { createAction, props } from '@ngrx/store';
import { Kind, Duplikatwarnung, Klasse, LoesungszettelResponse } from '@minikaenguru-ws/common-components';
import { Message } from '@minikaenguru-ws/common-messages';
import { Teilnahmeart, TeilnahmeIdentifierAktuellerWettbewerb } from '@minikaenguru-ws/common-components';
import { KindEditorVorbelegung, KlassenwechselDaten } from '../kinder.model';

export const teilnahmenummerInitialized = createAction(
	'[KinderFacade] on loadKinder',
	props<{ teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb }>()
);

export const startLoading = createAction(
	'[KinderFacade] before backendCall'
);

export const finishedWithError = createAction(
	'[KinderFacade] on loadKinder error'
);

export const allKinderLoaded = createAction(
	'[KinderFacade] on loadKinder finished with success',
	props<{ kinder: Kind[] }>()
);

export const unselectKind = createAction(
	'[LoesungszettelFacade] cancelEditLoesungszettel'
);

export const createNewKind = createAction(
	'[KinderFacade] createNewKind',
	props<{ klasseUuid?: string }>()
);

export const selectKind = createAction(
	'[KinderFacade] selectKind',
	props<{ kind: Kind }>()
);

export const startEditingKind = createAction(
	'[KinderFacade] editKind',
	props<{ kind: Kind }>()
);

export const kindSaved = createAction(
	'[KinderFacade] insert or update kind',
	props<{ kind: Kind, outcome: Message }>()
);

export const duplikatGeprueft = createAction(
	'[KinderFacade] pruefeDuplikat',
	props<{ duplikatwarnung: Duplikatwarnung }>()

);

export const editCancelled = createAction(
	'[KinderFacade] cancelEditKind'
);

export const kindDeleted = createAction(
	'[KinderFacade] deleteKind',
	props<{ kind: Kind, outcome: Message }>()
);

export const kindLoesungszettelChanged = createAction(
	'[LoesungszettelFacade]: change the punkte in saveLoesungszettel',
	props<{ kind: Kind, loesungszettelResponse: LoesungszettelResponse }>()
);

export const kindLoesungszettelDeleted = createAction(
	'[LoesungszettelFacade]: delete the punkte in deleteLoesungszettel',
	props<{ kindID: string }>()
);

export const resetModule = createAction(
	'[KinderFacade] resetState'
);


