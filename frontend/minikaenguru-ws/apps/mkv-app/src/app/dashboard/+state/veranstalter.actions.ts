import {createAction, props} from '@ngrx/store';

export const loadTeilnahmenummern = createAction(
    "[Dashboard Resolver] Load All teilnahmenummern"
);


export const allTeilnahmenummernLoaded = createAction(
    "[Load teilnahmenummern Effect] All teilnahmenummern loaded",
    props<{teilnahmenummern: string}>()
);

