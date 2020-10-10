import { createAction, props } from '@ngrx/store';
import { SchuleAdminOverview } from '../schulteilnahmen.model';



export const startLoadSchule = createAction(
	'[SchulteilnahmenFacade] findOrLoad beforeLoad'
);

export const loadSchuleFinishedWithError = createAction(
	'[SchulteilnahmenFacade] findOrLoad error'
);

export const schuleOverviewLoaded = createAction(
	'[SchulteilnahmenFacade] findOrLoadSchuleAdminOverview',
	props<{schuleAdminOverview: SchuleAdminOverview}>()
);


export const resetSchulteilnahmen = createAction(
	'[NavbarComponent] - schulteilnahmen login/logout'
);



