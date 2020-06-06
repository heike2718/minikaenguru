import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subscription, Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap, filter } from 'rxjs/operators';
import { KatalogItem, Katalogtyp } from '../domain/entities';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';
import { SchulkatalogFacade } from '../application-services/schulkatalog.facade';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { InternalFacade } from '../application-services/internal.facade';

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

	schulkatalogState$: Observable<SchulkatalogState> = this.schulkatalogFacade.schulkatalogState$;

	private schulkatalogStateSubscription: Subscription;

	constructor(@Inject(SchulkatalogConfigService) private config,
		public schulkatalogFacade: InternalFacade) { }

	ngOnInit() {

		this.schulkatalogStateSubscription = this.schulkatalogState$.subscribe(

			model => {
				this.selectedKatalogtyp = model.guiModel.currentKatalogtyp;
				this.selectedKatalogItem = model.selectedKatalogItem;
				this.searchFormInputValue = model.searchTerm;
			}

		);

		this.devMode = this.config.devmode;

		this.searchTerm = new BehaviorSubject<string>('');

		this.searchTerm.pipe(
			debounceTime(500),
			distinctUntilChanged(),
			filter(term => term.length > 2),
			tap(term => {
				this.startSearch(term)
			})
		).subscribe();
	}

	private startSearch(term: string): void {
		this.schulkatalogFacade.startSearch(this.selectedKatalogtyp, this.selectedKatalogItem, term);
	}

	ngOnDestroy() {
		if (this.schulkatalogStateSubscription) {
			this.schulkatalogStateSubscription.unsubscribe();
		}
	}

	onKeyup($event) {

		const btn = $event.target;

		const value = $event.target.value;
		this.searchTerm.next(value);
	}
}
