import { createReducer, Action, on } from '@ngrx/store';
import * as DownloadActions from './download.actions';

export const downloadFeatureKey = 'mk-download';

export interface DownloadState {
	downloadInProgress: boolean;
}

const initialDownloadState: DownloadState = {
	downloadInProgress: false
};

const downloadReducer = createReducer(initialDownloadState,

	on(DownloadActions.startDownload, (state, _action) => {
		return { ...state, downloadInProgress: true }
	}),
	on(DownloadActions.downloadFinished, (state, _action) => {
		return { ...state, downloadInProgress: false }
	}),);

export function reducer(state: DownloadState | undefined, action: Action) {

	return downloadReducer(state, action);

};

