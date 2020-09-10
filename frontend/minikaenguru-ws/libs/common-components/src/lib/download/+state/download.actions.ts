import { createAction, props } from '@ngrx/store';

export const startDownload = createAction(
	'[DownloadFacade] before request'
);

export const downloadFinished = createAction(
	'[DownloadFacade] after response'
);
