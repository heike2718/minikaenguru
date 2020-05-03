import { createAction, props } from '@ngrx/store';
import { Session } from '../domain/entities';


export const login = createAction(
	'[Navbar] login',
	props<{session: Session}>()
);

export const logout = createAction(
	'[Navbar/AuthService] logout'
);

export const refreshSession = createAction(
	'[AuthService] initSessionFromStorage',
	props<{session: Session}>()
);

