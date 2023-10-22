import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { AdminSchulkatalogFacade } from '../../admin-schulkatalog.facade';
import { AdminSchulkatalogConfigService } from '../../configuration/schulkatalog-config';
import { Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
	selector: 'mka-laender-list',
	templateUrl: './laender-list.component.html',
	styleUrls: ['./laender-list.component.css']
})
export class LaenderListComponent implements OnInit, OnDestroy {

	#config = inject(AdminSchulkatalogConfigService);
	katalogFacade = inject(AdminSchulkatalogFacade);

	devMode = this.#config.devmode;

	laender$ = this.katalogFacade.laender$;

	#kuerzelSubscription = new Subscription();
	#neueSchuleClicked = false;
	#neueSchuleDisabled = false;

	ngOnInit(): void {

		this.#kuerzelSubscription = this.katalogFacade.kuerzel$.pipe(
			tap((kuerzel) => {
				if (this.#neueSchuleClicked) {
					this.#neueSchuleClicked = false;
					this.katalogFacade.startCreateLandOrtUndSchule(kuerzel);
					this.#neueSchuleDisabled = false;
				}
			})
		).subscribe();
	}

	ngOnDestroy(): void {
		this.#kuerzelSubscription.unsubscribe();
	}

	neueSchule(): void {
		this.#neueSchuleDisabled = true;
        this.#neueSchuleClicked = true;
        this.katalogFacade.triggerCreateKuerzel();
	}
}
