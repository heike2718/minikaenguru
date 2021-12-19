import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subscription, Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap, filter } from 'rxjs/operators';
import { KatalogItem, Katalogtyp } from '../domain/entities';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';
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

	searchTerm!: BehaviorSubject<string>;

	searchFormInputValue!: string;

	private selectedKatalogtyp!: Katalogtyp;

	private selectedKatalogItem?: KatalogItem;

	schulkatalogState$: Observable<SchulkatalogState> = this.schulkatalogFacade.schulkatalogState$;

	private schulkatalogStateSubscription: Subscription = new Subscription();

	constructor(@Inject(SchulkatalogConfigService) public readonly config : any,
		public schulkatalogFacade: InternalFacade, private router: Router) { }

	ngOnInit() {

		this.schulkatalogStateSubscription = this.schulkatalogState$.subscribe(

			state => {
				this.selectedKatalogtyp = state.currentKatalogtyp;
				this.selectedKatalogItem = state.selectedKatalogItem;
				this.searchFormInputValue = state.searchTerm;
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

		this.schulkatalogFacade.startSearch(term, this.selectedKatalogtyp, this.selectedKatalogItem);
	}

	ngOnDestroy() {
		this.schulkatalogStateSubscription.unsubscribe();
	}

	onKeyup($event: any, context: string) {

		if (context !== 'KATALOGE') {
			return;
		}

		const value = $event.target.value;
		this.searchTerm.next(value);
	}

	handleNichtGefunden(): void {
		this.router.navigateByUrl('/antragsformular');
	}
}
