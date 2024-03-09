import { createReducer, Action, on } from '@ngrx/store';
import { AktuelleMeldung, initialAktuelleMeldung } from '../aktuelle-meldung.model';
import * as AktuelleMeldungActions from './aktuelle-meldung.actions';
export const aktuelleMeldungFeatureKey = 'mkv-app-aktuelle-meldung';

export interface AktuelleMeldungState {
	readonly aktuelleMeldung: AktuelleMeldung;
	readonly aktuelleMeldungLoaded: boolean;
	readonly aktuelleMeldungNotEmpty: boolean
};

const initialAktuelleMeldungState: AktuelleMeldungState = {
	aktuelleMeldung: initialAktuelleMeldung,
	aktuelleMeldungLoaded: false,
	aktuelleMeldungNotEmpty: true
};

const aktuelleMeldungReducer = createReducer(initialAktuelleMeldungState,

	on(AktuelleMeldungActions.aktuelleMeldungGeladen, (state, action) => {

		const meldung = action.aktuelleMeldung;
		const leer = meldung.text === undefined || meldung.text.length === 0;

		return { ...state, aktuelleMeldung: meldung, aktuelleMeldungLoaded: true, aktuelleMeldungNotEmpty: !leer }
	}),

	on(AktuelleMeldungActions.aktuelleMeldungGespeichert, (state, action) => {

		const meldung = action.aktuelleMeldung;
		const leer = meldung.text === undefined || meldung.text.length === 0;

		return { ...state, aktuelleMeldung: meldung, aktuelleMeldungLoaded: true, aktuelleMeldungNotEmpty: !leer }
	}),

	on(AktuelleMeldungActions.aktuelleMeldungGeloescht, (state, _action) => {

		const meldung = initialAktuelleMeldung;

		return { ...state, aktuelleMeldung: meldung, aktuelleMeldungLoaded: true, aktuelleMeldungNotEmpty: false }
	}),

	on(AktuelleMeldungActions.resetAktuelleMeldung, (_state, _action) => {

		return initialAktuelleMeldungState;
	})

);

export function reducer(state: AktuelleMeldungState | undefined, action: Action) {

	return aktuelleMeldungReducer(state, action);

};
