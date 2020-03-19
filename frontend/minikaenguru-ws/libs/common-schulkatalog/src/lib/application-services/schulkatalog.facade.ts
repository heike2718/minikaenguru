import { Injectable, Inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { KatalogService } from '../infrastructure/katalog.service';
import { selectKatalogItems, selectKatalogtyp, selectLoadingIndicator, selectSelectedKatalogItem, selectSearchTerm } from '../+state/schulkatalog.selectors';
import { Katalogtyp } from '../domain/entities';
import { HttpErrorResponse } from '@angular/common/http';
import { searchError, katalogItemsLoaded } from '../+state/schulkatalog.actions';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { LogService } from '@minikaenguru-ws/common-logging';
import { SchulkatalogConfig, SchulkatalogConfigService } from '../configuration/schulkatalog-config';

@Injectable({ providedIn: 'root' })
export class SchulkatalogFacade {

	public katalogItems$ = this.store.select(selectKatalogItems);
	public katalogtyp$ = this.store.select(selectKatalogtyp);
	public loading$ = this.store.select(selectLoadingIndicator);
	public selectedKatalogItem$ = this.store.select(selectSelectedKatalogItem);
	public searchTerm$ = this.store.select(selectSearchTerm);

	constructor(@Inject(SchulkatalogConfigService) private config: SchulkatalogConfig, private store: Store<SchulkatalogState>, private katalogService: KatalogService, private messagesService: MessageService
		, private logger: LogService) { }


	public searchKatalogItemsNeu(typ: Katalogtyp, searchTerm: string) {

		this.katalogService.searchKatalogItemsNeu(typ, searchTerm).subscribe(
			katalogItems => {
				this.store.dispatch(katalogItemsLoaded({ data: katalogItems }));
			},
			(error => {
				this.handleError(error, '[SchulkatalogFacade] searchKatalogItems')
			})
		);
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
							this.store.dispatch(searchError({ data: msg.message }));
						} else {
							this.store.dispatch(searchError({ data: 'unerwarteter Fehler ' + httpError.status }));
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
