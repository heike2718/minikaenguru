import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, concatMap } from 'rxjs/operators';
import { EMPTY, of } from 'rxjs';

import * as SchulkatalogActions from './schulkatalog.actions';

@Injectable()
export class SchulkatalogEffects {

	constructor(private actions$: Actions) {}

	loadKatalogItems$ = createEffect(() => {

return this.actions$.pipe(

	ofType(SchulkatalogActions.selectKatalogItem)
)

	})


}
