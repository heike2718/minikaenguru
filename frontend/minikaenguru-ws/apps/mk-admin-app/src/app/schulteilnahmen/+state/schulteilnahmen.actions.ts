import { createAction, props } from '@ngrx/store';
import { SchuleAdminOverview, AuswertungImportReport } from '../schulteilnahmen.model';
import { Teilnahme } from '@minikaenguru-ws/common-components';
import { UploadMonitoringInfo } from '../../uploads/uploads.model';

export const startLoadSchule = createAction(
	'[SchulteilnahmenFacade] findOrLoad beforeLoad'
);

export const loadFinishedWithError = createAction(
	'[SchulteilnahmenFacade] findOrLoad error'
);

export const schuleOverviewLoaded = createAction(
	'[SchulteilnahmenFacade] findOrLoadSchuleAdminOverview',
	props<{schuleAdminOverview?: SchuleAdminOverview}>()
);

export const startLoadUploadInfos = createAction(
	'[SchulteilnahmenFacade] loadUploadsInfos beforeLoad'
);


export const uploadsKlassenlisteInfosLoaded = createAction(
	'[SchulteilnahmenFacade] loadUploadKlassenlisteInfos',
	props<{uploadInfos: UploadMonitoringInfo[]}>()
);

export const anonymisierteTeilnahmeSelected = createAction(
	'[SchulteilnahmenFacade] selectTeilnahme',
	props<{teilnahme: Teilnahme}>()
);

export const auswertungImportiert = createAction(
	'[SchulteilnahmenFacade] auswertungImportiert',
	props<{report: AuswertungImportReport}>()
);

export const dateiAusgewaehlt = createAction(
	'[SchulteilnahmeFacade] dateiAusgewaehlt'
);

export const resetSchulteilnahmen = createAction(
	'[NavbarComponent] - schulteilnahmen login/logout'
);





