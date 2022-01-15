import { createAction, props } from '@ngrx/store';
import { StatistikGruppeninfo } from '../statistik.model';

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
    props<{statistik?: StatistikGruppeninfo}>()
);

export const collapsStatistik = createAction(
    '[StatistikFacade] collapse'
);

export const resetStatistiken = createAction(
    '[StatistikFacade] resetModule'
);
