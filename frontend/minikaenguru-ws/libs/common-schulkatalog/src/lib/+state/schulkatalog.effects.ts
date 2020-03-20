import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { tap } from 'rxjs/operators';

import * as SchulkatalogActions from './schulkatalog.actions';
import { SchulkatalogFacade } from '../application-services/schulkatalog.facade';

@Injectable()
export class SchulkatalogEffects {

	startSearch$ = createEffect(
		() => this.actions$.pipe(

			ofType(SchulkatalogActions.startSearch),
			tap((action) => {
				if (action.katalogItem.kuerzel) {
					this.schulkatalogFacade.searchKindelemente(action.katalogItem, action.searchTerm);
				} else {
					this.schulkatalogFacade.searchKatalogItems(action.katalogItem.typ, action.searchTerm)
				}
			})

		),
		{ dispatch: false }
	)

	katalogItemSelected$ = createEffect(
		() => this.actions$.pipe(

			ofType(SchulkatalogActions.katalogItemSelected),
			tap(action => {
				const selectedItem = action.katalogItem;

				if (selectedItem.typ == 'SCHULE') {
					// nüscht
				} else {
					if (selectedItem.anzahlKinder <= 10) {
						this.schulkatalogFacade.loadKindelemente(selectedItem);
					}
				}
			})
		),
		{ dispatch: false }
	);

	constructor(private actions$: Actions, private schulkatalogFacade: SchulkatalogFacade) { }
}
