import { createAction, props } from '@ngrx/store';

export const startDownload = createAction(
	'[DownloadFacade] before request',
	props<{id: string}>()
);

export const downloadFinished = createAction(
	'[DownloadFacade] after response'
);
