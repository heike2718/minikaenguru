import { Injectable } from '@angular/core';
import { Actions, ofType, createEffect } from '@ngrx/effects';
import { AuthActions } from './auth-action-types';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { STORAGE_KEY_USER } from './model/user.model';


@Injectable()
export class AuthEffects {

	login$ = createEffect(() => this.actions$.pipe(
		ofType(AuthActions.loadUser),
		tap(action => {
			localStorage.setItem(STORAGE_KEY_USER, JSON.stringify(action.user));
		})
	), { dispatch: false });
	// dispatch-property: no further action is going to be dispatched back to the store.
	// So infinite dispatch loops are going to be avoided

	logout$ = createEffect(() => this.actions$.pipe(
		ofType(AuthActions.logOut),
		tap(() => {
			localStorage.removeItem(STORAGE_KEY_USER);
		})
	), { dispatch: false });

	constructor(private actions$: Actions, private router: Router) { }

}
