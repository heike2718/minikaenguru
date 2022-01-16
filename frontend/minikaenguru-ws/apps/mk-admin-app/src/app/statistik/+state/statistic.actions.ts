import { createAction, props } from '@ngrx/store';
import { StatistikEntity, StatistikGruppeninfo, StatistikGruppenitem } from '../statistik.model';

export const startLoading = createAction(
    '[StatistikFacade] any action'
);

export const loadFinishedWithError = createAction(
    '[StatistikFacade] error on load'
);

export const statistikLoaded = createAction(
    '[StatistikFacade] load any',
    props<{statistik: StatistikGruppeninfo}>()
);

export const expandStatistik = createAction(
    '[StatistikFacade] expand',
    props<{entity: StatistikEntity, statistik?: StatistikGruppeninfo}>()
);

export const collapsStatistik = createAction(
    '[StatistikFacade] collapse',
    props<{entity: StatistikEntity}>()
);

export const resetStatistiken = createAction(
    '[StatistikFacade] resetModule'
);
