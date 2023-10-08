import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';

import * as KatalogpflegeActions from '../katalogpflege/+state/katalogpflege.actions';
import { KatalogHttpService } from '../services/katalog-http.service';
import { Katalogpflegetyp, KatalogpflegeItem, DeprecatedSchulePayload, KuerzelAPIModel, DeprecatedOrtPayload, DeprecatedLandPayload } from './katalogpflege.model';
import { Router } from '@angular/router';
import { laender, orte, schulen, selectedItem, editSchuleInput, editOrtInput, editLandInput } from './+state/katalogpflege.selectors';
import { tap, take } from 'rxjs/operators';
import { MessageService, Message } from '@minikaenguru-ws/common-messages';
import { SchuleEditorModel } from './+state/katalogpflege.reducer';
import { AuthService } from '@minikaenguru-ws/common-auth';


@Injectable()
export class KatalogpflegeFacade {

	public laender$ = this.store.select(laender);
	public orte$ = this.store.select(orte);
	public schulen$ = this.store.select(schulen);
	public selectedKatalogItem$ = this.store.select(selectedItem);
	public editSchuleInput$ = this.store.select(editSchuleInput);
	public editOrtInput$ = this.store.select(editOrtInput);
	public editLandInput$ = this.store.select(editLandInput);

	private loggingOut: boolean = false;

	constructor(private katalogHttpService: KatalogHttpService,
		private authService: AuthService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService,
		private store: Store<AppState>,
		private router: Router) {

			this.authService.onLoggingOut$.subscribe(
				loggingOut => this.loggingOut = loggingOut
			);
		}


	public selectKatalogpflegeTyp(typ: Katalogpflegetyp): void {

		this.store.dispatch(KatalogpflegeActions.selectKatalogTyp({ typ: typ }));
		switch (typ) {
			case 'LAND': this.router.navigateByUrl('/katalogpflege/laender'); break;
			case 'ORT': this.router.navigateByUrl('/katalogpflege/orte'); break;
			case 'SCHULE': this.router.navigateByUrl('/katalogpflege/schulen'); break;
		}
	}

	public ladeLaender(): void {

		if (this.loggingOut) {
			return;
		}

		this.katalogHttpService.loadLaender().subscribe(
			theLaender => {
				this.store.dispatch(KatalogpflegeActions.loadLaenderFinished({ laender: theLaender }));
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.finishedWithError());
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
		
		this.katalogHttpService.searchKatalogItems(typ, searchTerm).subscribe(
			items => {
				this.store.dispatch(KatalogpflegeActions.sucheFinished({ typ: typ, katalogItems: items }));
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.finishedWithError());
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
				this.store.dispatch(KatalogpflegeActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

	public switchToRenameKatalogItemEditor(item?: KatalogpflegeItem) {

		let url = '/katalogpflege';
		
		if (item) {
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
							kuerzelLand: item.parent? item.parent.kuerzel : '',
							nameLand: item.parent ? item.parent.name : ''
					}
				}));
				break;
			}
				case 'SCHULE':
				{
						const schulePayload: DeprecatedSchulePayload = {
							emailAuftraggeber: '',
							kuerzel: item.kuerzel,
							name: item.name,
							kuerzelLand: item.parent && item.parent.parent ? item.parent.parent.kuerzel : '',
							nameLand: item.parent && item.parent.parent ? item.parent.parent.name : '',
							kuerzelOrt: item.parent? item.parent.kuerzel : '',
							nameOrt: item.parent ? item.parent.name : ''
						};

						const schuleEditorModel: SchuleEditorModel = {
							kuerzelLandDisabled: true,
							modusCreate: false,
							nameLandDisabled: true,
							nameOrtDisabled: true,
							schulePayload: schulePayload
						};

						this.store.dispatch(KatalogpflegeActions.schulePayloadCreated({ schuleEditorModel: schuleEditorModel}));
						break;
				}
		}

		this.router.navigateByUrl(url);

	}
	}




	public sendCreateSchule(payload: DeprecatedSchulePayload): void {

		this.katalogHttpService.createSchule(payload).subscribe(
			responsePayload => {
				this.store.dispatch(KatalogpflegeActions.editSchuleFinished({ schulePayload: responsePayload.data }));

				const message: Message = responsePayload.message;

				switch (message.level) {
					case 'INFO': this.messageService.info(message.message); break;
					case 'WARN': this.messageService.warn(message.message); break;
				}
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);


	}

	public sendRenameSchule(schulePayload: DeprecatedSchulePayload): void {

		this.katalogHttpService.renameSchule(schulePayload).subscribe(
			responsePayload => {
				this.store.dispatch(KatalogpflegeActions.editSchuleFinished({ schulePayload: responsePayload.data }));

				const message: Message = responsePayload.message;

				switch (message.level) {
					case 'INFO': this.messageService.info(message.message); break;
					case 'WARN': this.messageService.warn(message.message); break;
				}
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

	public sendRenameOrt(ortPayload: DeprecatedOrtPayload) {

		this.katalogHttpService.renameOrt(ortPayload).subscribe(
			responsePayload => {
				this.store.dispatch(KatalogpflegeActions.editOrtFinished({ ortPayload: responsePayload.data }));

				const message: Message = responsePayload.message;

				switch (message.level) {
					case 'INFO': this.messageService.info(message.message); break;
					case 'WARN': this.messageService.warn(message.message); break;
				}
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);

	}

	public sendRenameLand(landPayload: DeprecatedLandPayload) {

		this.katalogHttpService.renameLand(landPayload).subscribe(
			responsePayload => {
				this.store.dispatch(KatalogpflegeActions.editLandFinished({ landPayload: responsePayload.data }));

				const message: Message = responsePayload.message;

				switch (message.level) {
					case 'INFO': this.messageService.info(message.message); break;
					case 'WARN': this.messageService.warn(message.message); break;
				}
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);

	}



	// ================= private methods ========================================//

	createTheNeueSchuleEditorModel(kuerzelAPIModel: KuerzelAPIModel, item?: KatalogpflegeItem): SchuleEditorModel {

		let kuerzelLandDisabled = false;
		let nameLandDisabled = false;
		let nameOrtDisabled = false;

		let payload: DeprecatedSchulePayload = {
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
					kuerzelLand: item.parent ? item.parent.kuerzel : '',
					nameLand: item.parent ? item.parent.name : '',
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

	ladeKinder(parent: KatalogpflegeItem) {

		if (this.loggingOut) {
			return;
		}

		this.katalogHttpService.loadChildItems(parent).subscribe(
			items => {
				this.store.dispatch(KatalogpflegeActions.loadChildItemsFinished({ parent: parent, katalogItems: items }));
				this.store.dispatch(KatalogpflegeActions.selectKatalogItem({ katalogItem: parent }));
			},
			(error => {
				this.store.dispatch(KatalogpflegeActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}
}

