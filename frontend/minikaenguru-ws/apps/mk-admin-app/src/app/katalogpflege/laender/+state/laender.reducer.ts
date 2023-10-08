import { Action, createReducer, on } from '@ngrx/store';
import {KatalogpflegeItem} from '../../katalogpflege.model';
import * as laenderActions from '../../../admin-kataloge/laender/+state/laender.actions';

export const laenderFeatureKey = 'mk-admin-app-laender';

export interface LaenderState {
    readonly laenderGeladen: boolean;
    readonly laender: KatalogpflegeItem[];
    readonly selectedLand: KatalogpflegeItem | undefined;
};

const initialLaenderState: LaenderState = {
    laenderGeladen: false,
    laender: [],
    selectedLand: undefined
};

const laenderReducer = createReducer(initialLaenderState, 

    on(laenderActions.resetLaender, (_state, _action) => {

		return initialLaenderState;

	}),
    
    
);

export function reducer(state: LaenderState | undefined, action: Action) {
	return laenderReducer(state, action);
};