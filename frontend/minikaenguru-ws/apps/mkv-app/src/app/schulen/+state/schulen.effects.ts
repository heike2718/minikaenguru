import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatMap, map } from 'rxjs/operators';
import { SchulenService } from '../../services/schulen.service';
import * as SchulenActions from './schulen.actions';


@Injectable()
export class SchulenEffects {

	loadSchulen$ = createEffect(

		() => this.actions$.pipe(
			ofType(SchulenActions.loadSchulen),
			concatMap(_action => this.schulenService.findSchulen()),
			map(schulen => SchulenActions.schulenLoaded({ schulen: schulen }))
		)

	);

	constructor(private actions$: Actions, private schulenService: SchulenService) { }
}
