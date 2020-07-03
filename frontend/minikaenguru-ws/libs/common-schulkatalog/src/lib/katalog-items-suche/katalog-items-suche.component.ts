import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subscription, Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap, filter } from 'rxjs/operators';
import { KatalogItem, Katalogtyp } from '../domain/entities';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';
import { SchulkatalogFacade } from '../application-services/schulkatalog.facade';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { InternalFacade } from '../application-services/internal.facade';
import { Router } from '@angular/router';

@Component({
	// tslint:disable-next-line: component-selector
	selector: 'mk-katalog-items-suche',
	templateUrl: './katalog-items-suche.component.html',
	styleUrls: ['./katalog-items-suche.component.css']
})
export class KatalogItemsSucheComponent implements OnInit, OnDestroy {

	searchTerm: BehaviorSubject<string>;

	searchFormInputValue: string;

	private selectedKatalogtyp: Katalogtyp;

	private selectedKatalogItem: KatalogItem;

	schulkatalogState$: Observable<SchulkatalogState> = this.schulkatalogFacade.schulkatalogState$;

	private schulkatalogStateSubscription: Subscription;

	constructor(@Inject(SchulkatalogConfigService) public readonly config,
		public schulkatalogFacade: InternalFacade, private router: Router) { }

	ngOnInit() {

		this.schulkatalogStateSubscription = this.schulkatalogState$.subscribe(

			model => {
				this.selectedKatalogtyp = model.currentKatalogtyp;
				this.selectedKatalogItem = model.selectedKatalogItem;
				this.searchFormInputValue = model.searchTerm;
			}

		);

		this.searchTerm = new BehaviorSubject<string>('');

		this.searchTerm.pipe(
			debounceTime(1000),
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

	gotoAntragsformular() : void {
		this.router.navigateByUrl('/antragsformular');
	}
}
