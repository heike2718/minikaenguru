import { Injectable } from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import * as VeranstalterActions from './veranstalter.actions';
import { VeranstalterService } from '../../services/veranstalter.service';
import { concatMap, map } from 'rxjs/operators';


@Injectable()
export class VeranstalterEffects {

	loadTeilnahmenummern$ = createEffect(
		() => this.actions$.pipe(
			ofType(VeranstalterActions.loadTeilnahmenummern),
			concatMap(_action => this.veranstalterService.findTeilnahmenummern()),
			map(nummern => VeranstalterActions.allTeilnahmenummernLoaded({teilnahmenummern: nummern}))
		)
	);

	constructor(private actions$: Actions, private veranstalterService: VeranstalterService){}

}
