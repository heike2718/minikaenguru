import { createAction, props } from '@ngrx/store';



export const dateSubmitted = createAction(
	'[EventlogFacade] submit date',
	props<{ datum: string }>()
);

export const dateCleared = createAction(
	'[EventlogFacade] clear date'
);
