import { Action, createReducer, on } from '@ngrx/store';
import { Mustertext } from '../../shared/shared-entities.model';
import { MustertextWithID } from '../mustertexte.model';
import * as MustertexteActions from './mustertexte.actions';

export const mustertexteFeatureKey = 'mk-admin-app-mustertexte';

export interface MustertexteState {
    readonly mustertexteMap: MustertextWithID[];
    readonly selectedMustertext?: Mustertext;
    readonly mustertextEditorModel?: Mustertext;
	readonly mustertexteLoaded: boolean;
	readonly loading: boolean;
};

const initialMustertexteState: MustertexteState = {
    mustertexteMap: [],
    selectedMustertext: undefined,
    mustertextEditorModel: undefined,
    mustertexteLoaded: false,
    loading: false
};

const mustertexteReducer = createReducer(initialMustertexteState, 
    
    on(MustertexteActions.resetMustertexte, (_state, _action) => {

		return initialMustertexteState;

	}),

);

export function reducer(state: MustertexteState | undefined, action: Action) {
    return mustertexteReducer(state, action);
};
