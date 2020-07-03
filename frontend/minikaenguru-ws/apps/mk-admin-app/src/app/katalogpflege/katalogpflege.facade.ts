import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';

import * as KatalogflegeActions from '../katalogpflege/+state/katalogpflege.actions';
import { KatalogHttpService } from '../services/katalog-http.service';
import { Katalogpflegetyp, KatalogpflegeItem } from './katalogpflege.model';
import { Router } from '@angular/router';
import { laender } from './+state/katalogpflege.selectors';


@Injectable()
export class KatalogpflegeFacade {

	public laender$ = this.store.select(laender);

	constructor(private katalogHttpService: KatalogHttpService,
		private errorHandler: GlobalErrorHandlerService,
		private store: Store<AppState>,
		private router: Router) {}


	public selectKatalogpflegeTyp(typ: Katalogpflegetyp): void {

		this.store.dispatch(KatalogflegeActions.selectKatalogTyp({typ: typ}));
		switch(typ) {
			case 'LAND': this.router.navigateByUrl('/katalogpflege/laender'); break;
			case 'ORT': this.router.navigateByUrl('/katalogpflege/orte'); break;
			case 'SCHULE': this.router.navigateByUrl('/katalogpflege/schulen'); break;
		}

	}


	public ladeLaender(): void {

		this.store.dispatch(KatalogflegeActions.startSuche());

		this.katalogHttpService.loadLaender().subscribe(
			laender => {
				this.store.dispatch(KatalogflegeActions.sucheFinished({ katalogItems: laender }));
			},
			(error => {
				this.store.dispatch(KatalogflegeActions.sucheFinishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

}

