import { createReducer, Action, on } from '@ngrx/store';
import * as DownloadActions from './download.actions';

export const downloadFeatureKey = 'mk-download';

export interface DownloadState {
	downloadInProgress: boolean;
	id: string;
}

const initialDownloadState: DownloadState = {
	downloadInProgress: false,
	id: undefined
};

const downloadReducer = createReducer(initialDownloadState,

	on(DownloadActions.startDownload, (state, action) => {
		return { ...state, downloadInProgress: true, id: action.id  }
	}),
	on(DownloadActions.downloadFinished, (state, _action) => {
		return { ...state, downloadInProgress: false, id: undefined }
	}),);

export function reducer(state: DownloadState | undefined, action: Action) {

	return downloadReducer(state, action);

};

