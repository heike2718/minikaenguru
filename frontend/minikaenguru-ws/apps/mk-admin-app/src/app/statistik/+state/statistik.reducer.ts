import { Action, createReducer, on } from '@ngrx/store';
import { StatistikGruppeninfo, StatistikGruppeninfoMap, StatistikGruppeninfoWithID } from '../statistik.model';
import * as StatistikActions from './statistic.actions';

export const statistikFeatureKey = 'mk-admin-app-statistik';

export interface StatistikState {
    readonly loading: boolean;
    readonly statistiken: StatistikGruppeninfoWithID[];
    readonly statistikKinder: StatistikGruppeninfo | undefined;
    readonly statistikLoesungszettel: StatistikGruppeninfo | undefined;    
};

const initialStatistikState: StatistikState = {
    loading: false,
    statistiken: [],
    statistikKinder: undefined,
    statistikLoesungszettel: undefined
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

        switch(action.entity) {
            case 'KINDER': return {...state, statistikKinder: action.statistik};
            case 'LOESUNGSZETTEL' : return {...state, statistikLoesungszettel: action.statistik};
            default: return {...state};
        }
    }),

    on (StatistikActions.collapsStatistik, (state, action) => {

        switch(action.entity) {
            case 'KINDER': return {...state, statistikKinder: undefined};
            case 'LOESUNGSZETTEL' : return {...state, statistikLoesungszettel: undefined};
            default: return {...state};
        }
    }),

	on(StatistikActions.resetStatistiken, (_state, _action) => {

		return initialStatistikState;

	}),

);

export function reducer(state: StatistikState | undefined, action: Action) {

	return statistikReducer(state, action);
}

