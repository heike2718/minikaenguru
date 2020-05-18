import { createAction, props } from '@ngrx/store';
import { Schule } from '../schulen.model';




export const loadSchulenByKuerzel = createAction(
	"[Schulen Resolver] load schulen",
	props<{schulkuerzelCommaseparated: string}>()
);


export const schulenLoaded = createAction(
    "[Load Schulen Effect] All Schulen Loaded",
    props<{schulen: Schule[]}>()
);
