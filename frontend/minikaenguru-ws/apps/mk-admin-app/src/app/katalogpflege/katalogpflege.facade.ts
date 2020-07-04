import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';

import * as KatalogpflegeActions from '../katalogpflege/+state/katalogpflege.actions';
import { KatalogHttpService } from '../services/katalog-http.service';
import { Katalogpflegetyp, KatalogpflegeItem } from './katalogpflege.model';
import { Router } from '@angular/router';
import { laender, orte, schulen, selectedItem } from './+state/katalogpflege.selectors';


@Injectable()
export class KatalogpflegeFacade {

	public laender$ = this.store.select(laender);
	public orte$ = this.store.select(orte);
	public schulen$ = this.store.select(schulen);
	public selectedKatalogItem$ = this.store.select(selectedItem);

	constructor(private katalogHttpService: KatalogHttpService,
		private errorHandler: GlobalErrorHandlerService,
		private store: Store<AppState>,
		private router: Router) { }


	public selectKatalogpflegeTyp(typ: Katalogpflegetyp): void {

		this.store.dispatch(KatalogpflegeActions.selectKatalogTyp({ typ: typ }));
		switch (typ) {
			case 'LAND': this.router.navigateByUrl('/katalogpflege/laender'); break;
			case 'ORT': this.router.navigateByUrl('/katalogpflege/orte'); break;
			case 'SCHULE': this.router.navigateByUrl('/katalogpflege/schulen'); break;
		}
	}

	public ladeLaender(): void {

		this.store.dispatch(KatalogpflegeActions.startSuche());

		this.katalogHttpService.loadLaender().subscribe(
			laender => {
				this.store.dispatch(KatalogpflegeActions.loadLaenderFinished({ laender: laender }));
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.sucheFinishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

	public resetSelection() {

		this.store.dispatch(KatalogpflegeActions.resetSelection());

	}

	public gotoEditor(item: KatalogpflegeItem) {

		this.store.dispatch(KatalogpflegeActions.selectKatalogItem({ katalogItem: item }));

		let url = '/katalogpflege';
		switch (item.typ) {
			case 'LAND': url += '/land-'; break;
			case 'ORT': url += '/ort-'; break;
			case 'SCHULE': url += '/schule-'; break;
		}
		url += 'editor/' + item.kuerzel;


		this.router.navigateByUrl(url);
	}

	public gotoChildItems(parent: KatalogpflegeItem) {

		if (parent.typ !== 'SCHULE') {

			if (!parent.kinderGeladen && parent.anzahlKinder <= 25) {
				this.ladeKinder(parent);
			} else {
				this.store.dispatch(KatalogpflegeActions.selectKatalogItem({ katalogItem: parent }));
			}
		}

		let url = '/katalogpflege';
		switch (parent.typ) {
			case 'LAND': url += '/orte'; break;
			case 'ORT': url += '/schulen'; break;
		}

		this.router.navigateByUrl(url);
	}

	ladeKinder(item: KatalogpflegeItem) {

		this.store.dispatch(KatalogpflegeActions.startSuche());

		this.katalogHttpService.loadChildItems(item).subscribe(
			items => {
				this.store.dispatch(KatalogpflegeActions.loadChildItemsFinished({ parent: item, katalogItems: items }));
				this.store.dispatch(KatalogpflegeActions.selectKatalogItem({ katalogItem: item }));
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.sucheFinishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}
}

