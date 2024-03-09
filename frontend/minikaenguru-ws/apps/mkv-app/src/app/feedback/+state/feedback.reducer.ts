import { Action, createReducer, on } from "@ngrx/store";
import { BewertungAufgabe, BewertungAufgabeGUIModel, BewertungsbogenGUIModel, BewertungsbogenKlassenstufe, createBewertungAufgabe, createBewertungAufgabeGUIModel } from "../feedback.model";
import * as FeedbacActions from './feedback.actions';
import { getKlassenstufeByLabel } from "@minikaenguru-ws/common-components";

export const feedbackFeatureKey = 'mkv-app-feedback';

export interface FeedbackState {

    readonly bewertungsbogenCreated: boolean;
    readonly bewertungsbogenEINSSubmitted: boolean;
    readonly bewertungsbogenZWEISubmitted: boolean;
    readonly bewertungsboegenSubmitted: boolean;
    readonly guiModel?: BewertungsbogenGUIModel;
}

const initialFeedbackState: FeedbackState = {
    bewertungsbogenCreated: false,
    bewertungsbogenEINSSubmitted: false,
    bewertungsbogenZWEISubmitted: false,
    bewertungsboegenSubmitted: false,
    guiModel: undefined
};

const feedbackReducer = createReducer(initialFeedbackState,

    on(FeedbacActions.aufgabenvorschauGeladen, (state, action) => {

        const theKlassenstufe = getKlassenstufeByLabel(action.aufgabenvorschau.klassenstufe);
        const bewertungen: BewertungAufgabe[] = action.aufgabenvorschau.aufgaben.map(a => createBewertungAufgabe(a));
        const bewertungenAufgabeGUIModel: BewertungAufgabeGUIModel[] = action.aufgabenvorschau.aufgaben.map(a => createBewertungAufgabeGUIModel(a));

        const bewertungsbogen: BewertungsbogenKlassenstufe = {
            klassenstufe: theKlassenstufe.klassenstufe,
            scoreSpassfaktor: 0,
            scoreZufriedenheit: 0,
            bewertungenAufgaben: bewertungen,
            freitext: ''
        }

        const guiModel: BewertungsbogenGUIModel = {
            klassenstufe: action.aufgabenvorschau.klassenstufe,
            bewertungsbogen: bewertungsbogen,
            items: bewertungenAufgabeGUIModel
        }

        return { ...state, guiModel: guiModel, bewertungsbogenCreated: true }
    }),
    on(FeedbacActions.submitBewertung, (state, action) => {

        const theKlassenstufe = action.bewertungsbogen.klassenstufe;
        const einSubmitted = theKlassenstufe === "EINS";
        const zweiSubmitted = theKlassenstufe === "ZWEI";
        return {...state, bewertungsbogenEINSSubmitted: einSubmitted, bewertungsbogenZWEISubmitted: zweiSubmitted};
    }),
    on(FeedbacActions.bewertungSubmitted, (state, _action) => {

        const isBeideSubmitted = state.bewertungsbogenEINSSubmitted && state.bewertungsbogenZWEISubmitted;

        return {...state, bewertungsboegenSubmitted: isBeideSubmitted};
    }),
    on(FeedbacActions.resetState, (_state, _action) => {

        return initialFeedbackState;
    }),
);

export function reducer(state: FeedbackState | undefined, action: Action) {

    return feedbackReducer(state, action);

};