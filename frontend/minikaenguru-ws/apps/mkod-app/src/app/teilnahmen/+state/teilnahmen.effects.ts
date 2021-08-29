import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatMap, map } from 'rxjs/operators';
import { TeilnahmenFacade } from '../teilnahmen.facade';
import { loadTeilnhahmen, teilnahmenLoaded } from './teilnahmen.actions'

@Injectable()
export class TeilnahmenEffects {

    loadTeilnahmen$ = createEffect(

        () => this.actions$.pipe(
			ofType(loadTeilnhahmen),
			concatMap(action => this.teilnahmenFacade.loadTeilnahmen(action.jahr)
            ),
			map(anmeldungen => teilnahmenLoaded({ anmeldungen: anmeldungen }))
		)
    );


    constructor(private actions$: Actions, private teilnahmenFacade: TeilnahmenFacade) { }

}
