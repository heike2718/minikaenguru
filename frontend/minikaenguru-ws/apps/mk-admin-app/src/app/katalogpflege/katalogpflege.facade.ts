import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';

import * as KatalogpflegeActions from '../katalogpflege/+state/katalogpflege.actions';
import { KatalogHttpService } from '../services/katalog-http.service';
import { Katalogpflegetyp, KatalogpflegeItem, SchulePayload, KuerzelAPIModel, mergeKatalogItemMap } from './katalogpflege.model';
import { Router } from '@angular/router';
import { laender, orte, schulen, selectedItem, editSchuleInput } from './+state/katalogpflege.selectors';
import { tap, concat, mergeMap, concatMap } from 'rxjs/operators';
import { MessageService } from '@minikaenguru-ws/common-messages';


@Injectable()
export class KatalogpflegeFacade {

	public laender$ = this.store.select(laender);
	public orte$ = this.store.select(orte);
	public schulen$ = this.store.select(schulen);
	public selectedKatalogItem$ = this.store.select(selectedItem);
	public editSchuleInput$ = this.store.select(editSchuleInput);

	constructor(private katalogHttpService: KatalogHttpService,
		private messageService: MessageService,
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

	public searchKatalogItems(typ: Katalogpflegetyp, searchTerm: string) {
		this.store.dispatch(KatalogpflegeActions.startSuche());

		this.katalogHttpService.searchKatalogItems(typ, searchTerm).subscribe(
			items => {
				this.store.dispatch(KatalogpflegeActions.sucheFinished({ typ: typ, katalogItems: items }));
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.sucheFinishedWithError());
				this.errorHandler.handleError(error)
			})
		);

	}

	public clearRearchResults(): void {

		this.store.dispatch(KatalogpflegeActions.clearRearchResults());

	}

	public createNeueSchulePayload(): void {

		this.store.dispatch(KatalogpflegeActions.startSuche());

		this.katalogHttpService.getKuerzel().subscribe(
			kuerzelAPIModel => {
				this.selectedKatalogItem$.pipe(
					tap(
						item => {
							let payload: SchulePayload = this.createTheNeueSchulePayload(kuerzelAPIModel, item);
							this.store.dispatch(KatalogpflegeActions.neueSchulePayloadCreated({ payload: payload }));
						}
					)
				).subscribe();
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.sucheFinishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

	public createEditSchulePayload(katalogItem: KatalogpflegeItem) {

		this.store.dispatch(KatalogpflegeActions.startSuche());

		if (katalogItem) {
			const payload: SchulePayload = {
				name: katalogItem.name,
				kuerzel: katalogItem.kuerzel,
				kuerzelOrt: katalogItem.parent.kuerzel,
				nameOrt: katalogItem.parent.name,
				kuerzelLand: katalogItem.parent.parent.kuerzel,
				nameLand: katalogItem.parent.parent.name
			}

			this.store.dispatch(KatalogpflegeActions.editSchulePayloadCreated({payload: payload}));

		}
	}

	public sendCreateSchule(payload: SchulePayload): void {

		this.store.dispatch(KatalogpflegeActions.startSuche());

		// TODO
		this.store.dispatch(KatalogpflegeActions.neueSchuleSaved({katalogItem: undefined}));
		this.messageService.info('Neue Schule wurde erfolgreich angelegt');
	}

	public finishEditSchule(): void {
		this.store.dispatch(KatalogpflegeActions.editSchuleFinished());
		this.router.navigateByUrl('/katalogpflege/schulen');
	}

	// ================= private methods ========================================//

	createTheNeueSchulePayload(kuerzelAPIModel: KuerzelAPIModel, item: KatalogpflegeItem): SchulePayload {

		let payload: SchulePayload = {
			name: '',
			kuerzel: kuerzelAPIModel.kuerzelSchule,
			kuerzelOrt: kuerzelAPIModel.kuerzelOrt,
			nameOrt: '',
			kuerzelLand: '',
			nameLand: ''
		};

		if (item) {
			switch (item.typ) {
				case 'LAND':
					payload = {
						...payload,
						kuerzelLand: item.kuerzel,
						nameLand: item.name
					};
					break;
				case 'ORT': payload = {
					...payload,
					kuerzelLand: item.parent.kuerzel,
					nameLand: item.parent.name,
					kuerzelOrt: item.kuerzel,
					nameOrt: item.name
				};
					break;
				case 'SCHULE':
					break;
			}
		}

		return payload;
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

