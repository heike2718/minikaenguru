import { Action, createReducer, on } from '@ngrx/store';
import { Mustertext, MUSTRETEXT_KATEGORIE } from '../../shared/shared-entities.model';
import { MustertexteMap, MustertextWithID } from '../mustertexte.model';
import * as MustertexteActions from './mustertexte.actions';

export const mustertexteFeatureKey = 'mk-admin-app-mustertexte';

export interface MustertexteState {
    readonly mustertexteMap: MustertextWithID[];
    readonly filterKategorie: MUSTRETEXT_KATEGORIE;
    readonly filteredMustertexte: MustertextWithID[];
    readonly selectedMustertext?: Mustertext;
    readonly mustertextEditorModel?: Mustertext;
	readonly mustertexteLoaded: boolean;
	readonly loading: boolean;
};

const initialMustertexteState: MustertexteState = {
    mustertexteMap: [],
    filterKategorie: 'UNDEFINED',
    filteredMustertexte: [],
    selectedMustertext: undefined,
    mustertextEditorModel: undefined,
    mustertexteLoaded: false,
    loading: false
};

const mustertexteReducer = createReducer(initialMustertexteState, 
    
    on(MustertexteActions.startBackendCall, (state, _action) => {

		return {...state, loading: true};

	}),

    on(MustertexteActions.backendCallFinishedWithError, (state, _action) => {

		return {...state, loading: false};

	}),

   on(MustertexteActions.mustertexteLoaded, (state, action) => {

        const newMap: MustertextWithID[] = [];
        action.mustertexte.forEach( m => newMap.push({uuid: m.uuid, mustertext: m}));

        const filteredMap = [...newMap];
		return {...state, loading: false, mustertexteMap: newMap, filterKategorie: 'UNDEFINED', filteredMustertexte: filteredMap, mustertexteLoaded: true};

	}),

    
    on(MustertexteActions.filterkriteriumChanged, (state, action) => {

        const filteredMap = new MustertexteMap(state.mustertexteMap).filterByKategorie(action.neuerFilter);

        return {...state, filterKategorie: action.neuerFilter, filteredMustertexte: filteredMap};
	}),

    

    on(MustertexteActions.mustertextDeleted, (state, action) => {

        const newMap: MustertextWithID[] = new MustertexteMap(state.mustertexteMap).remove(action.mustertext);
        const filteredMap = new MustertexteMap(newMap).filterByKategorie(state.filterKategorie);

		return {...state, loading: false, mustertextEditorModel: undefined, selectedMustertext: undefined, mustertexteMap: newMap, filteredMustertexte: filteredMap};

	}),



    on(MustertexteActions.resetMustertexte, (_state, _action) => {

		return initialMustertexteState;

	}),

);

export function reducer(state: MustertexteState | undefined, action: Action) {
    return mustertexteReducer(state, action);
};
