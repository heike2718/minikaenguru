import { createAction, props } from '@ngrx/store';
import { TeilnahmeIdentifierAktuellerWettbewerb, Klasse, Duplikatwarnung, Kind } from '@minikaenguru-ws/common-components';
import { Message } from '@minikaenguru-ws/common-messages';
import { createAttribute } from '@angular/compiler/src/core';
import { KlassenlisteImportReport } from '../klassen.model';



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

export const startAssigningKinder = createAction(
	'[KlassenFacade] insertUpdateKinder',
	props<{klasse: Klasse}>()
);

export const kindAdded = createAction(
	'[KinderFacade] insertKind to klasse'
);

export const kindDeleted = createAction(
	'[KinderFacade] deleteKind from klasse',
	props<{kind: Kind}>()
);

export const klasseSaved = createAction(
	'[KlassenFacade] insert or update klasse',
	props<{klasse: Klasse}>()
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
	props<{klasse: Klasse}>()
);

export const kindMoved = createAction(
	'[KinderFacade] moveKind',
	props<{kind: Kind, sourceKlasseUuid: string, targetKlasseUuid: string}>()
);

export const loesungszettelAdded = createAction(
	'[LoesungszettelFacade] saveLoesungszettel second',
	props<{kind: Kind}>()
);

export const loesungszettelDeleted = createAction(
	'[LoesungszettelFacade] deleteLoesungszettel second',
	props<{kind: Kind}>()
);

export const dateiAusgewaehlt = createAction(
	'[KlassenFacade] klassenlisteAusgewaehlt'
);

export const klassenlisteImportiert = createAction(
	'[KlassenFacade] klassenlisteImportiert',
	props<{report: KlassenlisteImportReport}>()
);

export const resetModule = createAction(
	'[KlassenFacade] reset'
);



