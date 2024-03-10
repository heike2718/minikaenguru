import { Action, createReducer, on } from '@ngrx/store';
import * as NewsletterActions from './versandauftraege.actions';
import { Versandauftrag } from '../../shared/newsletter-versandauftrage.model';

export const versandauftraegeFeatureKey = 'mk-admin-app-versandauftraege';

export interface VersandauftraegeState {
	readonly versandauftraege: Versandauftrag[];
	readonly selecteVersandauftrag?: Versandauftrag;
};

const initialNewsletterState: VersandauftraegeState = {
	versandauftraege: [],
	selecteVersandauftrag: undefined
};

const newsletterReducer = createReducer(initialNewsletterState,

	on(NewsletterActions.backendCallFinishedWithError, (state, _action) => {

		return { ...state, loading: false };

	}),

	on(NewsletterActions.versandauftraegeLoaded, (state, action) => {

		return {...state, versandauftraege: action.versandauftraege};
	}),

	on(NewsletterActions.versandauftragSelected, (state, action) => {

		return {...state, selecteVersandauftrag: action.versandauftrag};
	}),

	on(NewsletterActions.versandauftragUnselected, (state, _action) => {

		return {...state, selecteVersandauftrag: undefined};

	}),

	on(NewsletterActions.resetVersandauftraege, (_state, _action) => {

		return initialNewsletterState;

	})

);

export function reducer(state: VersandauftraegeState | undefined, action: Action) {
	return newsletterReducer(state, action);
};


