import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatMap, map } from 'rxjs/operators';
import { WettbewerbeService } from '../../services/wettbewerbe.service';
import * as WettbewerbeActions from './wettbewerbe.actions';

@Injectable()
export class WettbewerbeEffects {


	loadWettbewerbe$ = createEffect(


		() => this.actions$.pipe(
			ofType(WettbewerbeActions.loadWettbewerbe),
			concatMap(_action => this.wettbewerbeService.loadWettbewerbe()),
			map(wettbewerbe => WettbewerbeActions.allWettbewerbeLoaded({wettbewerbe: wettbewerbe}))
		)

	);

	constructor(private actions$: Actions, private wettbewerbeService: WettbewerbeService){}
}
