import { Component, OnInit, Input, Inject, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap, filter } from 'rxjs/operators';
import { Katalogtyp, GuiModel, KatalogItem } from '../domain/entities';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';
import { SchulkatalogFacade } from '../application-services/schulkatalog.facade';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { initSucheComponentCompleted, startSearch } from '../+state/schulkatalog.actions';

@Component({
	// tslint:disable-next-line: component-selector
	selector: 'mk-katalog-items-suche',
	templateUrl: './katalog-items-suche.component.html',
	styleUrls: ['./katalog-items-suche.component.css']
})
export class KatalogItemsSucheComponent implements OnInit, OnDestroy {

	@Input()
	typ: string;

	devMode: boolean;

	private guiModelSubscription: Subscription;

	guiModel: GuiModel;

	searchTerm: BehaviorSubject<string>;

	searchFormInputValue: string;

	selectedKatalogItem: KatalogItem;

	private selectedKatalogItemSubscription: Subscription;

	private searchTermSubscription: Subscription;

	private katalogtyp: Katalogtyp = 'ORT';

	constructor(@Inject(SchulkatalogConfigService) private config,
		private store: Store<SchulkatalogState>,
		public schulkatalogFacade: SchulkatalogFacade) { }

	ngOnInit() {

		this.store.dispatch(initSucheComponentCompleted({ katalogtyp: this.katalogtyp }));

		this.searchTermSubscription = this.schulkatalogFacade.searchTerm$.subscribe(
			term => this.searchFormInputValue = term
		)

		this.guiModelSubscription = this.schulkatalogFacade.guiModel$.subscribe(
			model => this.guiModel = model
		);

		this.selectedKatalogItemSubscription = this.schulkatalogFacade.selectedKatalogItem$.subscribe(
			item => this.selectedKatalogItem = item
		);

		this.devMode = this.config.devmode;

		this.searchTerm = new BehaviorSubject<string>('');


		this.searchTerm.pipe(
			debounceTime(500),
			distinctUntilChanged(),
			filter(term => term.length > 2),
			tap(term => {

				if (this.selectedKatalogItem) {
					this.store.dispatch(startSearch({ katalogItem: this.selectedKatalogItem, searchTerm: term }));
				} else {
					const katalogItem = {
						typ: this.guiModel.currentKatalogtyp
					} as KatalogItem;
					this.store.dispatch(startSearch({ katalogItem: katalogItem, searchTerm: term }));
				}


			})
		).subscribe();
	}

	ngOnDestroy() {
		if (this.guiModelSubscription) {
			this.guiModelSubscription.unsubscribe;
		}
		if (this.selectedKatalogItemSubscription) {
			this.selectedKatalogItemSubscription.unsubscribe();
		}
		if (this.searchTermSubscription) {
			this.searchTermSubscription.unsubscribe();
		}
	}


	onKeyup($event) {
		const value = $event.target.value;
		console.log('[event.value=' + value + ']')
		this.searchTerm.next(value);
	}
}
