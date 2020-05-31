import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload, MessageService, Message } from '@minikaenguru-ws/common-messages';
import { Wettbewerb, WettbewerbEditorModel } from '../wettbewerbe/wettbewerbe.model';
import { createNewWettbewerb, wettbewerbSaved, selectWettbewerbsjahr, startEditingWettbewerb } from '../wettbewerbe/+state/wettbewerbe.actions';

import { LogService } from '@minikaenguru-ws/common-logging';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { wettbewerbe, selectedWettbewerb, wettbewerbEditorModel, saveOutcome } from '../wettbewerbe/+state/wettbewerbe.selectors';
import { Router } from '@angular/router';

@Injectable({
	providedIn: 'root'
})
export class WettbewerbFacade {

	public wettbewerbe$: Observable<Wettbewerb[]> = this.store.select(wettbewerbe);
	public wettbewerb$: Observable<Wettbewerb> = this.store.select(selectedWettbewerb);
	public wettbewerbEditorModel$: Observable<WettbewerbEditorModel> = this.store.select(wettbewerbEditorModel);
	public saveOutcome$: Observable<Message> = this.store.select(saveOutcome);
	public selectedJahr$: Observable<{ jahr: number }> = this.store.select(selectWettbewerbsjahr);



	constructor(private http: HttpClient,
		private store: Store<AppState>,
		private router: Router,
		private logger: LogService,
		private errorService: GlobalErrorHandlerService,
		private messageService: MessageService) {
	}

	public createNewWettbewerb(): void {
		this.store.dispatch(createNewWettbewerb());
	}

	public selectWettbewerb(wettbewerb: Wettbewerb): void {
		this.store.dispatch(selectWettbewerbsjahr({ jahr: wettbewerb.jahr }));
		this.router.navigateByUrl('/wettbewerbe/wettbewerb-dashboard/' + wettbewerb.jahr);
	}

	public editWettbewerb(wettbewerb: Wettbewerb): void {
		this.store.dispatch(startEditingWettbewerb({ wettbewerb: wettbewerb }))
		this.logger.debug('navigieren zum Editor für Jahr ' + wettbewerb.jahr);
		this.router.navigateByUrl('/wettbewerbe/wettbewerb-editor/' + wettbewerb.jahr);
	}


	public loadWettbewerbe(): Observable<Wettbewerb[]> {

		const url = environment.apiUrl + '/wb-admin/wettbewerbe';

		this.logger.debug(url);

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		)
	}

	public loadWettbewerbDetails(jahr: number): Observable<Wettbewerb> {

		const url = environment.apiUrl + '/wb-admin/wettbewerbe/wettbewerb/' + jahr;

		this.logger.debug(url);

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}

	public saveWettbewerb(wettbewerb: WettbewerbEditorModel): void {

		const url = environment.apiUrl + '/wb-admin/wettbewerbe/wettbewerb';

		const payload = {
			jahr: wettbewerb.jahr,
			status: wettbewerb.status.toString(),
			wettbewerbsbeginn: wettbewerb.wettbewerbsbeginn ? wettbewerb.wettbewerbsbeginn : null,
			wettbewerbsende: wettbewerb.wettbewerbsende,
			datumFreischaltungLehrer: wettbewerb.datumFreischaltungLehrer,
			datumFreischaltungPrivat: wettbewerb.datumFreischaltungPrivat
		};

		this.logger.debug(JSON.stringify(payload));

		this.http.post(url, payload).pipe(
			map(body => body as ResponsePayload),
		).subscribe(
			(responsePayload) => {
				this.messageService.info(responsePayload.message.message);
				this.store.dispatch(wettbewerbSaved({ wettbewerb: wettbewerb, outcome: responsePayload.message }));
			},
			(error) => {
				const message: Message = {
					level: 'ERROR',
					message: this.errorService.extractMessageObject(error).message
				};
				this.store.dispatch(wettbewerbSaved({ wettbewerb: wettbewerb, outcome: message }));
			}
		);
	}
}
