import { Injectable } from '@angular/core';
import { Actions, ofType, createEffect } from '@ngrx/effects';
import { AuthActions } from './action-types';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';


@Injectable()
export class AuthEffects {

	login$ = createEffect(() => this.actions$.pipe(
		ofType(AuthActions.login),
		tap(action => localStorage.setItem('user', JSON.stringify(action.user)))
	), { dispatch: false });
	// dispatch-property: no further action is goingto be dispatched back to the store.
	// So infinite dispatch loops are going to be avoided

	logout$ = createEffect(() => this.actions$.pipe(
		ofType(AuthActions.logout),
		tap(() => {
			localStorage.removeItem('user');
			this.router.navigateByUrl('/dashboard');
		})
	), { dispatch: false });

	constructor(private actions$: Actions, private router: Router) { }

}
