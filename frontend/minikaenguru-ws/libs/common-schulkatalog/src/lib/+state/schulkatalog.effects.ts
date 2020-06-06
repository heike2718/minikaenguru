import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { tap } from 'rxjs/operators';

import * as SchulkatalogActions from './schulkatalog.actions';
import { InternalFacade } from '../application-services/internal.facade';

@Injectable()
export class SchulkatalogEffects {

	startSearch$ = createEffect(
		() => this.actions$.pipe(

			ofType(SchulkatalogActions.startSearch),
			tap((action) => {
				if (action.selectedItem) {
					this.internalFacade.searchKindelemente(action.selectedItem, action.searchTerm);
				} else {
					this.internalFacade.searchKatalogItems(action.selectedKatalogtyp, action.searchTerm)
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
						this.internalFacade.loadKindelemente(selectedItem);
					}
				}
			})
		),
		{ dispatch: false }
	);

	constructor(private actions$: Actions, private internalFacade: InternalFacade) { }
}
