import { createAction, props } from '@ngrx/store';
import { TeilnahmeIdentifierAktuellerWettbewerb, Klasse, Duplikatwarnung } from '@minikaenguru-ws/common-components';
import { Message } from '@minikaenguru-ws/common-messages';



export const teilnahmenummerInitialized = createAction(
	'[KlassenFacade] on loadKlassen',
	props<{ teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb }>()
);

export const startLoading = createAction(
	'[KlassenFacade] before backendCall'
);

export const finishedLoadig = createAction(
	'[KlassenFacade] after backendCall'
);

export const allKlassenLoaded = createAction(
	'[KlassenFacade] loadKlassen success',
	props<{ klassen: Klasse[] }>()
);

export const createNewKlasse = createAction(
	'[KlassenFacade] startCreateKlasse'
);

export const startEditingKlasse = createAction(
	'[KlassenFacade] editKlasse',
	props<{klasse: Klasse}>()
);

export const klasseSaved = createAction(
	'[KlassenFacade] insert or update klasse',
	props<{klasse: Klasse, message: Message}>()
);

export const duplikatGeprueft = createAction(
	'[KlassenFacade] pruefeDuplikat',
	props<{duplikatwarnung: Duplikatwarnung}>()
);

export const editCancelled = createAction(
	'[KlassenFacade] cancelEdit'
);

export const klasseDeleted = createAction(
	'[KlassenFacade] deleteKlasse',
	props<{klasse: Klasse, outcome: Message}>()
);

export const resetModule = createAction(
	'[KlassenFacade] reset'
);



