import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { ClientActions } from './client-action-types';
import { tap } from 'rxjs/operators';
import { STORAGE_KEY_CLIENT } from './model/client.model';
import { AuthService } from '../auth/auth.service';
import { STORAGE_KEY_JWT } from 'hewi-ng-lib';


@Injectable()
export class ClientEffects {

	initClientAccessToken$ = createEffect(() =>
		this.actions$
			.pipe(
				ofType(ClientActions.initAccessToken),
				tap(action => localStorage.setItem(STORAGE_KEY_CLIENT, JSON.stringify(action.client)))
			),
		{ dispatch: false });

	refreshClientAccessToken$ = createEffect(() =>

		this.actions$.pipe(
			ofType(ClientActions.refereshAccessToken),
			tap(action => localStorage.setItem(STORAGE_KEY_CLIENT, JSON.stringify(action.client)))
		), { dispatch: false }
	);

	initJWT$ = createEffect(() =>
		this.actions$.pipe(
			ofType(ClientActions.initJWT),
			tap(action => localStorage.setItem(STORAGE_KEY_JWT, JSON.stringify(action.jwt)))
		), { dispatch: false }
	);

	logout$ = createEffect(() =>
		this.actions$.pipe(
			ofType(ClientActions.logout),
			tap(() => {
				localStorage.removeItem(STORAGE_KEY_JWT);
			})
		), { dispatch: false }
	);

	deleteJWT$$ = createEffect(() =>
		this.actions$.pipe(
			ofType(ClientActions.deleteJWT),
			tap(() => {
				localStorage.removeItem(STORAGE_KEY_JWT);
			})
		), { dispatch: false }
	);

	constructor(private actions$: Actions, private authService: AuthService) { }

}
