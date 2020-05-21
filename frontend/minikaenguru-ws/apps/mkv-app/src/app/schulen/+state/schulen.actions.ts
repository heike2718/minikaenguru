import { createAction, props } from '@ngrx/store';
import { Schule } from '../schulen.model';

export const loadSchulen = createAction(
	"[Schulen Resolver] load schulen"
);

export const schulenLoaded = createAction(
    "[Load Schulen Effect] All Schulen Loaded",
    props<{schulen: Schule[]}>()
);