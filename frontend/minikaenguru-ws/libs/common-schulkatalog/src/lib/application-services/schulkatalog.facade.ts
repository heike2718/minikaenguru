import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { KatalogService } from '../infrastructure/katalog.service';
import { selectKatalogItems, selectKatalogtyp, selectLoadingIndicator, selectSelectedKatalogItem } from '../+state/schulkatalog.selectors';
import { Katalogtyp } from '../domain/entities';
import { Observable } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { searchError, katalogItemsLoaded } from '../+state/schulkatalog.actions';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { LogService } from '@minikaenguru-ws/common-logging';

@Injectable({ providedIn: 'root' })
export class SchulkatalogFacade {

	public katalogItems$ = this.store.select(selectKatalogItems);
	public katalogtyp$ = this.store.select(selectKatalogtyp);
	public loading$ = this.store.select(selectLoadingIndicator);
	public selectedKatalogItem$ = this.store.select(selectSelectedKatalogItem);
	
	constructor(private store: Store<SchulkatalogState>, private katalogService: KatalogService, private messagesService: MessageService
		, private logger: LogService) { }


	public searchKatalogItems(katalogtyp: Katalogtyp, terms: Observable<string>) {

		this.katalogService.searchKatalogItems(katalogtyp, terms).subscribe(
			katalogItems => {
				this.store.dispatch(katalogItemsLoaded({data: katalogItems}))
			},
			(error => {
				this.store.dispatch(searchError);
				this.handleError(error, '[SchulkatalogFacade] searchKatalogItems')
			})
		);
	}


	handleError(error: HttpErrorResponse, context: string) {

		if (error instanceof ErrorEvent) {
			this.logger.error(context + ': ErrorEvent occured - ' + JSON.stringify(error));
			throw (error);
		} else {

			if (error.status === 0) {
				this.messagesService.error('Der Server ist nicht erreichbar.');
			} else {
				const msg = this.extractMessageObject(error);
				switch (error.status) {
					case 401:
					case 908:
						break;
					default: {
						if (msg) {
							this.showServerResponseMessage(msg);
						} else {
							this.messagesService.error(context + ' status=' + error.status
								+ ': OMG +++ Divide By Cucumber Error. Please Reinstall Universe And Reboot +++');
						}
					}
				}
			}
		}
	}

	private extractMessageObject(error: HttpErrorResponse): {level: string, message: string} {

		if (error.error && error.error.message) {
			return {level: 'ERROR', message: error.error.message};
		}

		return null;
	}

	private showServerResponseMessage(msg: {level: string, message: string}) {
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
