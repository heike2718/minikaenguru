import { Injectable, Inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { KatalogService } from '../infrastructure/katalog.service';
import { katalogItems, schulkatalogState, selectedKatalogItem } from '../+state/schulkatalog.selectors';
import { Katalogtyp, KatalogItem } from '../domain/entities';
import { HttpErrorResponse } from '@angular/common/http';
import { searchError, searchFinished, startLoadChildItems, childItemsLoaded, initSchulkatalog, katalogItemSelected } from '../+state/schulkatalog.actions';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { LogService } from '@minikaenguru-ws/common-logging';
import { SchulkatalogConfig, SchulkatalogConfigService } from '../configuration/schulkatalog-config';

@Injectable({ providedIn: 'root' })
export class SchulkatalogFacade {

	public katalogItems$ = this.store.select(katalogItems);
	public schulkatalogState$ = this.store.select(schulkatalogState);
	public selectedKatalogItem$ = this.store.select(selectedKatalogItem);

	constructor(@Inject(SchulkatalogConfigService) private config: SchulkatalogConfig, private store: Store<SchulkatalogState>, private katalogService: KatalogService, private messagesService: MessageService
		, private logger: LogService) { }


	public initSchulkatalog(typ: Katalogtyp): void {

		this.store.dispatch(initSchulkatalog({ katalogtyp: typ }));
	}


	public searchKatalogItems(typ: Katalogtyp, searchTerm: string) {

		this.katalogService.searchKatalogItems(typ, searchTerm).subscribe(
			katalogItems => {
				this.store.dispatch(searchFinished({ katalogItems: katalogItems, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }));
			},
			(error => {
				this.handleError(error, '[SchulkatalogFacade] searchKatalogItems')
			})
		);
	}

	public searchKindelemente(katalogItem: KatalogItem, searchTerm: string) {

		this.katalogService.searchKindelemente(katalogItem, searchTerm).subscribe(
			katalogItems => {
				this.store.dispatch(searchFinished({ katalogItems: katalogItems, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }));
			},
			(error => {
				this.handleError(error, '[SchulkatalogFacade] searchKatalogItems')
			})
		);
	}


	public loadKindelemente(katalogItem: KatalogItem) {

		this.store.dispatch(startLoadChildItems());

		this.katalogService.loadKindelemente(katalogItem).subscribe(
			katalogItems => {
				this.store.dispatch(childItemsLoaded({ katalogItems: katalogItems }));
			},
			(error => {
				this.handleError(error, '[SchulkatalogFacade] loadKindelemente')
			})
		);
	}

	public selectKatalogItem(katalogItem: KatalogItem): void {
		this.store.dispatch(katalogItemSelected({ katalogItem: katalogItem, immediatelyLoadOnNumberChilds: this.config.immediatelyLoadOnNumberChilds }))
	}


	handleError(error: any, context: string) {

		if (error instanceof ErrorEvent) {
			this.logger.error(context + ': ErrorEvent occured - ' + JSON.stringify(error));
			throw (error);
		} else {

			const httpError = error as HttpErrorResponse;

			if (httpError.status === 0) {
				this.messagesService.error('Der Server ist nicht erreichbar.');
			} else {
				const msg = this.extractMessageObject(httpError);
				switch (httpError.status) {
					case 401:
					case 908:
						break;
					default: {
						this.logger.error(context + ' url=' + httpError.url);
						if (msg) {
							this.showServerResponseMessage(msg);
							this.store.dispatch(searchError());
						} else {
							this.store.dispatch(searchError());
							if (this.config.admin) {
								this.messagesService.error(context + ' status=' + error.status
									+ ': OMG +++ Divide By Cucumber Error. Please Reinstall Universe And Reboot +++');
							} else {
								this.messagesService.error('Es ist ein unerwarteter Fehler aufgetreten. Bitte schreiben Sie eine Mail an minikaenguru@egladil.de');
							}
						}
					}
				}
			}
		}
	}

	private extractMessageObject(error: HttpErrorResponse): { level: string, message: string } {

		if (error.error && error.error.message) {
			return { level: 'ERROR', message: error.error.message };
		}

		return null;
	}

	private showServerResponseMessage(msg: { level: string, message: string }) {
		switch (msg.level) {
			case 'WARN':
				this.messagesService.error(msg.message);
				break;
			case 'ERROR':
				this.messagesService.error(msg.message);
				break;
			default:
				this.messagesService.error('Unbekanntes message.level ' + msg.level + ' vom Server bekommen.');
		}
	}
}
