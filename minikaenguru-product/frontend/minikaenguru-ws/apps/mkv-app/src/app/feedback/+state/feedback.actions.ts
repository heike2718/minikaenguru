import { Klassenstufenart } from "@minikaenguru-ws/common-components";
import { createAction, props } from "@ngrx/store";
import { Aufgabenvorschau, BewertungsbogenKlassenstufe } from "../feedback.model";
import { Message } from "@minikaenguru-ws/common-messages";


export const loadAufabenvorschau = createAction(
	'[FeedbackFacade] loadAufabenvorschau',
	props<{klassenstufe: Klassenstufenart}>()
);

export const aufgabenvorschauGeladen = createAction(
	'[FeedbackEffect] loadAufabenvorschau',
	props<{aufgabenvorschau: Aufgabenvorschau}>()
);

export const submitBewertung = createAction(
	'[FeedbackFacade]: submitBewertung',
	props<{bewertungsbogen: BewertungsbogenKlassenstufe}>()
);

export const bewertungSubmitted = createAction(
	'[FeedbackEffect] submitBewertung',
	props<{message: Message}>()
);

export const resetState = createAction(
	'[FeedbackFacade] resetState()'
);


