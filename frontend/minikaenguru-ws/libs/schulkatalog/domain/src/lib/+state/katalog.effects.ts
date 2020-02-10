import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, concatMap } from 'rxjs/operators';
import { EMPTY, of } from 'rxjs';

import * as KatalogActions from './katalog.actions';



@Injectable()
export class KatalogEffects {

  loadKatalogs$ = createEffect(() => {
    return this.actions$.pipe( 

      ofType(KatalogActions.loadKatalogs),
      concatMap(() =>
        /** An EMPTY observable only emits completion. Replace with your own observable API request */
        EMPTY.pipe(
          map(data => KatalogActions.loadKatalogsSuccess({ data })),
          catchError(error => of(KatalogActions.loadKatalogsFailure({ error }))))
      )
    );
  });



  constructor(private actions$: Actions) {}

}
