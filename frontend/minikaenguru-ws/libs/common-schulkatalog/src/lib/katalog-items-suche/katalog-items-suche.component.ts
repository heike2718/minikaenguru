import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap, filter } from 'rxjs/operators';
import { KatalogItem, Katalogtyp } from '../domain/entities';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';
import { SchulkatalogFacade } from '../application-services/schulkatalog.facade';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { startSearch } from '../+state/schulkatalog.actions';

@Component({
	// tslint:disable-next-line: component-selector
	selector: 'mk-katalog-items-suche',
	templateUrl: './katalog-items-suche.component.html',
	styleUrls: ['./katalog-items-suche.component.css']
})
export class KatalogItemsSucheComponent implements OnInit, OnDestroy {

	devMode: boolean;

	searchTerm: BehaviorSubject<string>;

	searchFormInputValue: string;

	private selectedKatalogtyp: Katalogtyp;

	private selectedKatalogItem: KatalogItem;

	private selectedKatalogtypSubscription: Subscription;

	private selectedKatalogItemSubscription: Subscription;

	private searchTermSubscription: Subscription;

	constructor(@Inject(SchulkatalogConfigService) private config,
		private store: Store<SchulkatalogState>,
		public schulkatalogFacade: SchulkatalogFacade) { }

	ngOnInit() {

		this.searchTermSubscription = this.schulkatalogFacade.searchTerm$.subscribe(
			term => this.searchFormInputValue = term
		)

		this.selectedKatalogtypSubscription = this.schulkatalogFacade.selectedKatalogtyp$.subscribe(
			typ => this.selectedKatalogtyp = typ
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
				this.store.dispatch(startSearch({ selectedKatalogtyp: this.selectedKatalogtyp, selectedItem: this.selectedKatalogItem, searchTerm: term }));
			})
		).subscribe();
	}

	ngOnDestroy() {
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
