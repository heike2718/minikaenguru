import { AboutInfos } from './model/about.model';
import { AboutActions } from './about-action-types';
import { createReducer, on, State, Action } from '@ngrx/store';

export const aboutFeatureKey = 'about';

// store arrays as key-value-maps see 29.understanding the NgRx Entity Format
export interface AboutInfoState {
	aboutInfos: AboutInfos;
}

export const initialAboutInfosState: AboutInfoState = {
	aboutInfos: undefined
};

// Macht Problem beim AOT build, siehe https://github.com/ngrx/platform/issues/1915
export const aboutReducer = createReducer(
	initialAboutInfosState,
	on(AboutActions.aboutInfosLoaded, (_state, action) => {
		return {
			aboutInfos: action.about
		};
	})
);

// see https://github.com/ngrx/platform/issues/1915
// and Udemy course comment https://www.udemy.com/course/ngrx-course/learn/lecture/15942130#questions/8441474
export function aboutReducerWrapper(state, action) {
	return aboutReducer(state, action);
}



