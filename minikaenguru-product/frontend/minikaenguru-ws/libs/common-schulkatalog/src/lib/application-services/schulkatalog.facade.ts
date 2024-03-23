import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { selectedKatalogItem, selectedItemAndSelectedTyp } from '../+state/schulkatalog.selectors';
import { Katalogtyp } from '../domain/entities';
import { initSchulkatalog } from '../+state/schulkatalog.actions';

@Injectable({ providedIn: 'root' })
export class SchulkatalogFacade {

	public selectedKatalogItem$ = this.store.select(selectedKatalogItem);
	public selectedItemAndSelectedTyp$ = this.store.select(selectedItemAndSelectedTyp);

	constructor(private store: Store<SchulkatalogState>) {}


	public initSchulkatalog(typ: Katalogtyp): void {

		this.store.dispatch(initSchulkatalog({ katalogtyp: typ }));
	}
}
