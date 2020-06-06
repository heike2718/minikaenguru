import { Injectable, Inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { KatalogService } from '../infrastructure/katalog.service';
import { katalogItems, schulkatalogState, selectedKatalogItem } from '../+state/schulkatalog.selectors';
import { Katalogtyp, KatalogItem } from '../domain/entities';
import { searchFinished, startLoadChildItems, childItemsLoaded, katalogItemSelected, startSearch } from '../+state/schulkatalog.actions';
import { SchulkatalogConfig, SchulkatalogConfigService } from '../configuration/schulkatalog-config';
import { ErrorHandlerService } from '../infrastructure/error-handler.service';

@Injectable({ providedIn: 'root' })
export class InternalFacade {

	public katalogItems$ = this.store.select(katalogItems);
	public schulkatalogState$ = this.store.select(schulkatalogState);
	public selectedKatalogItem$ = this.store.select(selectedKatalogItem);

	constructor(@Inject(SchulkatalogConfigService) private config: SchulkatalogConfig,
		private store: Store<SchulkatalogState>,
		private katalogService: KatalogService,
		private errorHandler: ErrorHandlerService) { }


	public startSearch(katalogtyp: Katalogtyp, katalogItem: KatalogItem, searchTerm: string) {
		this.store.dispatch(startSearch({
			selectedKatalogtyp: katalogtyp,
			selectedItem: katalogItem,
			searchTerm: searchTerm
		}));
	}

	public searchKatalogItems(typ: Katalogtyp, searchTerm: string) {

		this.katalogService.searchKatalogItems(typ, searchTerm).subscribe(
			katalogItems => {
				this.store.dispatch(searchFinished({ katalogItems: katalogItems, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }));
			},
			(error => {
				this.errorHandler.handleError(error, '[SchulkatalogFacade] searchKatalogItems')
			})
		);
	}

	public searchKindelemente(katalogItem: KatalogItem, searchTerm: string) {

		this.katalogService.searchKindelemente(katalogItem, searchTerm).subscribe(
			katalogItems => {
				this.store.dispatch(searchFinished({ katalogItems: katalogItems, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }));
			},
			(error => {
				this.errorHandler.handleError(error, '[SchulkatalogFacade] searchKatalogItems')
			})
		);
	}


	public loadKindelemente(katalogItem: KatalogItem) {

		this.store.dispatch(startLoadChildItems());

		this.katalogService.loadKindelemente(katalogItem).subscribe(
			katalogItems => {
				this.store.dispatch(childItemsLoaded({ katalogItems: katalogItems }));
			},
			(error => {
				this.errorHandler.handleError(error, '[SchulkatalogFacade] loadKindelemente')
			})
		);
	}

	public selectKatalogItem(katalogItem: KatalogItem): void {
		this.store.dispatch(katalogItemSelected({ katalogItem: katalogItem, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }))
	}
}
