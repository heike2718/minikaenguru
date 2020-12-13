import { Injectable, Inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { KatalogService } from '../infrastructure/katalog.service';
import { katalogItems, schulkatalogState, selectedKatalogItem, katalogAntragSuccess } from '../+state/schulkatalog.selectors';
import { Katalogtyp, KatalogItem, SchulkatalogAntrag } from '../domain/entities';
import { searchFinished, startLoadChildItems, childItemsLoaded, katalogItemSelected, startSearch } from '../+state/schulkatalog.actions';
import { SchulkatalogConfig, SchulkatalogConfigService } from '../configuration/schulkatalog-config';
import { ErrorHandlerService } from '../infrastructure/error-handler.service';
import { MessageService } from '@minikaenguru-ws/common-messages';
import * as SchulkatalogActions from '../+state/schulkatalog.actions';

@Injectable({ providedIn: 'root' })
export class InternalFacade {

	public katalogItems$ = this.store.select(katalogItems);
	public schulkatalogState$ = this.store.select(schulkatalogState);
	public selectedKatalogItem$ = this.store.select(selectedKatalogItem);
	public katalogAntragSuccess$ = this.store.select(katalogAntragSuccess);

	constructor(@Inject(SchulkatalogConfigService) private config: SchulkatalogConfig,
		private store: Store<SchulkatalogState>,
		private katalogService: KatalogService,
		private messageService: MessageService,
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
			katItems => {
				this.store.dispatch(searchFinished({ katalogItems: katItems, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }));
			},
			(error => {
				this.errorHandler.handleError(error, '[InternalFacade] searchKatalogItems')
			})
		);
	}

	public searchKindelemente(katalogItem: KatalogItem, searchTerm: string) {

		this.katalogService.searchKindelemente(katalogItem, searchTerm).subscribe(
			katItems => {
				this.store.dispatch(searchFinished({ katalogItems: katItems, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }));
			},
			(error => {
				this.errorHandler.handleError(error, '[InternalFacade] searchKatalogItems')
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
				this.errorHandler.handleError(error, '[InternalFacade] loadKindelemente')
			})
		);
	}

	public selectKatalogItem(katalogItem: KatalogItem): void {
		this.store.dispatch(katalogItemSelected({ katalogItem: katalogItem, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }))
	}

	public submitSchulkatalogAntrag(antrag: SchulkatalogAntrag): void {

		this.katalogService.submitSchulkatalogAntrag(antrag).subscribe(
			message => {
				this.messageService.info(message.message);
				this.store.dispatch(SchulkatalogActions.katalogantragSuccessfullySubmitted());
			},
			(error => {
				this.errorHandler.handleError(error, '[InternalFacade] submitSchulkatalogAntrag')
			})
		);

	}
}
