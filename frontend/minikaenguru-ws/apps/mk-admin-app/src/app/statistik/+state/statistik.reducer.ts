import { Action, createReducer, on } from '@ngrx/store';
import { StatistikService } from '../statistic.service';
import { StatistikGruppeninfo, StatistikGruppeninfoMap, StatistikGruppeninfoWithID } from '../statistik.model';
import * as StatistikActions from './statistic.actions';

export const statistikFeatureKey = 'mk-admin-app-statistik';

export interface StatistikState {
    readonly loading: boolean;
    readonly statistiken: StatistikGruppeninfoWithID[];
    readonly expandedStatistik: StatistikGruppeninfo | undefined;    
};

const initialStatistikState: StatistikState = {
    loading: false,
    statistiken: [],
    expandedStatistik: undefined
};

const statistikReducer = createReducer(initialStatistikState,

    on (StatistikActions.startLoading, (state, _action) => {
        return {...state, loading: true}
    }),

    on (StatistikActions.loadFinishedWithError, (state, _action) => {

        return {...state, loading: false};
    }),

    on (StatistikActions.statistikLoaded, (state, action) => {

        const neueStatistiken = new StatistikGruppeninfoMap(state.statistiken).merge(action.statistik);
        return {...state, loading: false, statistiken: neueStatistiken};
    }),

    on (StatistikActions.expandStatistik, (state, action) => {

        return {...state, expandedStatistik: action.statistik};
    }),

    on (StatistikActions.collapsStatistik, (state, _action) => {

        return {...state, expandedStatistik: undefined};
    }),

	on(StatistikActions.resetStatistiken, (_state, _action) => {

		return initialStatistikState;

	}),

);

export function reducer(state: StatistikState | undefined, action: Action) {

	return statistikReducer(state, action);
}

