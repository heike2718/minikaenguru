import { AboutInfos } from './model/about.model';
import { AboutActions } from './about-action-types';
import { createReducer, on } from '@ngrx/store';

export const aboutFeatureKey = 'about';

// store arrays as key-value-maps see 29.understanding the NgRx Entity Format
export interface AboutInfoState {
	aboutInfos: AboutInfos;
}

export const initialAboutInfosState: AboutInfoState = {
	aboutInfos: undefined
};

export const authReducer = createReducer(
	initialAboutInfosState,
	on(AboutActions.aboutInfosLoaded, (_state, action) => {
		return {
			aboutInfos: action.about
		};
	})
);

