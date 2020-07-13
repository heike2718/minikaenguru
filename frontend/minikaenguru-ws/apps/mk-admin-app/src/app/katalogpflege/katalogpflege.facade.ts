import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';

import * as KatalogpflegeActions from '../katalogpflege/+state/katalogpflege.actions';
import { KatalogHttpService } from '../services/katalog-http.service';
import { Katalogpflegetyp, KatalogpflegeItem, SchulePayload, KuerzelAPIModel, OrtPayload, LandPayload } from './katalogpflege.model';
import { Router } from '@angular/router';
import { laender, orte, schulen, selectedItem, editSchuleInput, editOrtInput, editLandInput } from './+state/katalogpflege.selectors';
import { tap, take } from 'rxjs/operators';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { SchuleEditorModel } from './+state/katalogpflege.reducer';


@Injectable()
export class KatalogpflegeFacade {

	public laender$ = this.store.select(laender);
	public orte$ = this.store.select(orte);
	public schulen$ = this.store.select(schulen);
	public selectedKatalogItem$ = this.store.select(selectedItem);
	public editSchuleInput$ = this.store.select(editSchuleInput);
	public editOrtInput$ = this.store.select(editOrtInput);
	public editLandInput$ = this.store.select(editLandInput);

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

		this.store.dispatch(KatalogpflegeActions.showLoadingIndicator());

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
		this.store.dispatch(KatalogpflegeActions.showLoadingIndicator());

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

	public switchToKataloge(): void {

		this.store.dispatch(KatalogpflegeActions.katalogDashboardSelected());
		this.router.navigateByUrl('/katalogpflege');

	}

	public switchToDashboard(): void {

		this.store.dispatch(KatalogpflegeActions.katalogDashboardSelected());
		this.router.navigateByUrl('/dashboard');

	}

	public clearSearchResults(): void {

		this.store.dispatch(KatalogpflegeActions.clearSearchResults());
	}

	public switchToCreateNeueSchuleEditor(): void {

		this.store.dispatch(KatalogpflegeActions.showLoadingIndicator());

		this.katalogHttpService.getKuerzel().subscribe(
			kuerzelAPIModel => {
				this.selectedKatalogItem$.pipe(
					tap(
						item => {
							const schuleEditorModel: SchuleEditorModel = this.createTheNeueSchuleEditorModel(kuerzelAPIModel, item);
							this.store.dispatch(KatalogpflegeActions.schulePayloadCreated({ schuleEditorModel: schuleEditorModel }));
							this.router.navigateByUrl('/katalogpflege/schule-editor');
						}
					),
					take(1)
				).subscribe();
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.sucheFinishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

	public switchToRenameKatalogItemEditor(item: KatalogpflegeItem) {

		let url = '/katalogpflege';
		switch (item.typ) {
			case 'LAND': url += '/land-'; break;
			case 'ORT': url += '/ort-'; break;
			case 'SCHULE': url += '/schule-'; break;
		}
		url += 'editor';

		this.store.dispatch(KatalogpflegeActions.selectKatalogItem({ katalogItem: item }));

		switch (item.typ) {
			case 'LAND': {
				this.store.dispatch(KatalogpflegeActions.landPayloadCreated({
					landPayload: {
						kuerzel: item.kuerzel,
						name: item.name
					}
				}));
				break;
			}
			case 'ORT': {
				this.store.dispatch(KatalogpflegeActions.ortPayloadCreated({
					ortPayload: {
						kuerzel: item.kuerzel,
						name: item.name,
						kuerzelLand: item.parent.kuerzel,
						nameLand: item.parent.name
					}
				}));
				break;
			}
			case 'SCHULE':
				{
					const kuerzelAPIModel: KuerzelAPIModel = {
						kuerzelOrt: item.parent.kuerzel,
						kuerzelSchule: item.kuerzel
					}
					this.store.dispatch(KatalogpflegeActions.schulePayloadCreated({ schuleEditorModel: this.createTheNeueSchuleEditorModel(kuerzelAPIModel, item) }));
					break;
				}
		}

		this.router.navigateByUrl(url);
	}



	public sendCreateSchule(payload: SchulePayload): void {

		this.store.dispatch(KatalogpflegeActions.showLoadingIndicator());

		// TODO: http

		this.store.dispatch(KatalogpflegeActions.editSchuleFinished({ schulePayload: payload }));
		this.messageService.info('Neue Schule wurde erfolgreich angelegt');
	}

	public sendRenameSchule(schulePayload: SchulePayload): void {

		this.store.dispatch(KatalogpflegeActions.showLoadingIndicator());

		// TODO: http
		this.store.dispatch(KatalogpflegeActions.editSchuleFinished({ schulePayload: schulePayload }));
		this.messageService.info('Schule wurde erfolgreich geändert');
	}

	public sendRenameOrt(ortPayload: OrtPayload) {

	}

	public sendRenameLand(landPayload: LandPayload) {

	}



	// ================= private methods ========================================//

	createTheNeueSchuleEditorModel(kuerzelAPIModel: KuerzelAPIModel, item: KatalogpflegeItem): SchuleEditorModel {

		let kuerzelLandDisabled: boolean = false;
		let nameLandDisabled: boolean = false;
		let nameOrtDisabled: boolean = false;

		let payload: SchulePayload = {
			name: '',
			kuerzel: kuerzelAPIModel.kuerzelSchule,
			kuerzelOrt: kuerzelAPIModel.kuerzelOrt,
			nameOrt: '',
			kuerzelLand: '',
			nameLand: '',
			emailAuftraggeber: ''
		};

		if (item) {
			switch (item.typ) {
				case 'LAND':
					payload = {
						...payload,
						kuerzelLand: item.kuerzel,
						nameLand: item.name
					};
					kuerzelLandDisabled = true;
					nameLandDisabled = true;
					break;
				case 'ORT': payload = {
					...payload,
					kuerzelLand: item.parent.kuerzel,
					nameLand: item.parent.name,
					kuerzelOrt: item.kuerzel,
					nameOrt: item.name
				};
					kuerzelLandDisabled = true;
					nameLandDisabled = true;
					nameOrtDisabled = true;

					break;
				case 'SCHULE':
					break;
			}
		}

		return {
			schulePayload: payload,
			modusCreate: true,
			kuerzelLandDisabled: kuerzelLandDisabled,
			nameLandDisabled: nameLandDisabled,
			nameOrtDisabled: nameOrtDisabled
		};
	}

	ladeKinder(item: KatalogpflegeItem) {

		this.store.dispatch(KatalogpflegeActions.showLoadingIndicator());

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

