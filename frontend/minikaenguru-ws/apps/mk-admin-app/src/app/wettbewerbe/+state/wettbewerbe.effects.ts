import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatMap, map } from 'rxjs/operators';
import { WettbewerbFacade } from '../../services/wettbewerb.facade';
import * as WettbewerbeActions from './wettbewerbe.actions';

@Injectable()
export class WettbewerbeEffects {


	loadWettbewerbe$ = createEffect(

		() => this.actions$.pipe(
			ofType(WettbewerbeActions.loadWettbewerbe),
			concatMap(_action => this.wettbewerbFacade.loadWettbewerbe()),
			map(wettbewerbe => WettbewerbeActions.allWettbewerbeLoaded({ wettbewerbe: wettbewerbe }))
		)

	);

	loadWettbewerbDetails$ = createEffect(

		() => this.actions$.pipe(
			ofType(WettbewerbeActions.loadWettbewerbDetails),
			concatMap(action => this.wettbewerbFacade.loadWettbewerbDetails(action.jahr)),
			map(wettbewerb => WettbewerbeActions.selectedWettbewerbLoaded({wettbewerb: wettbewerb}))

		)
	)

	constructor(private actions$: Actions, private wettbewerbFacade: WettbewerbFacade) { }
}
