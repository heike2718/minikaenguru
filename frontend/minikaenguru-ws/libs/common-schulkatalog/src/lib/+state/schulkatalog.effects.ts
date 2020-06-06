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
				if (action.selectedItem) {
					this.schulkatalogFacade.searchKindelemente(action.selectedItem, action.searchTerm);
				} else {
					this.schulkatalogFacade.searchKatalogItems(action.selectedKatalogtyp, action.searchTerm)
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
					if (selectedItem.anzahlKinder <= action.immediatelyLoadOnNumberChilds) {
						this.schulkatalogFacade.loadKindelemente(selectedItem);
					}
				}
			})
		),
		{ dispatch: false }
	);

	constructor(private actions$: Actions, private schulkatalogFacade: SchulkatalogFacade) { }
}
