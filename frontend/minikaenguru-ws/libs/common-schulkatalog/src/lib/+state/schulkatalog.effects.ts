import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, concatMap, tap } from 'rxjs/operators';
import { EMPTY, of } from 'rxjs';

import * as SchulkatalogActions from './schulkatalog.actions';
import { dispatch } from 'rxjs/internal/observable/pairs';
import { SchulkatalogFacade } from '../application-services/schulkatalog.facade';

@Injectable()
export class SchulkatalogEffects {

	loadKatalogItems$ = createEffect(
		() => this.actions$.pipe(
			ofType(SchulkatalogActions.selectKatalogItem),
			tap((action) => console.log('ktalog items loaded: ' + action.data.name))
		),
		{ dispatch: false }
		// FeatureActions.actionOne is not dispatched
	);

	searchTermChanged$ = createEffect(
		() => this.actions$.pipe(

			ofType(SchulkatalogActions.searchTermChanged),
			tap((action) => this.schulkatalogFacade.searchKatalogItemsNeu(action.katalogtyp, action.searchTerm) )

		),
		{ dispatch: false }
	)

	constructor(private actions$: Actions, private schulkatalogFacade: SchulkatalogFacade) { }


}
