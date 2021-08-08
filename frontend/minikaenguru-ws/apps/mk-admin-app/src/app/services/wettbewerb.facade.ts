import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload, MessageService, Message, ErrorMappingService } from '@minikaenguru-ws/common-messages';
import { Wettbewerb, WettbewerbEditorModel } from '../wettbewerbe/wettbewerbe.model';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { wettbewerbe, selectedWettbewerb, wettbewerbEditorModel, saveOutcome } from '../wettbewerbe/+state/wettbewerbe.selectors';
import { Router } from '@angular/router';

import * as WettbewerbActions from '../wettbewerbe/+state/wettbewerbe.actions';
import { AuthService } from '@minikaenguru-ws/common-auth';

@Injectable({
	providedIn: 'root'
})
export class WettbewerbFacade {

	public wettbewerbe$: Observable<Wettbewerb[]> = this.store.select(wettbewerbe);
	public wettbewerb$: Observable<Wettbewerb> = this.store.select(selectedWettbewerb);
	public wettbewerbEditorModel$: Observable<WettbewerbEditorModel> = this.store.select(wettbewerbEditorModel);
	public saveOutcome$: Observable<Message> = this.store.select(saveOutcome);

	private loggingOut: boolean;

	constructor(private http: HttpClient,
		private authService: AuthService,
		private store: Store<AppState>,
		private router: Router,
		private logger: LogService,
		private errorMapper: ErrorMappingService,
		private errorService: GlobalErrorHandlerService,
		private messageService: MessageService) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);
	}

	public createNewWettbewerb(): void {
		this.store.dispatch(WettbewerbActions.createNewWettbewerb());
	}

	public selectWettbewerb(wettbewerb: Wettbewerb): void {
		this.store.dispatch(WettbewerbActions.selectWettbewerbsjahr({ jahr: wettbewerb.jahr }));
		this.router.navigateByUrl('/wettbewerbe/wettbewerb-dashboard/' + wettbewerb.jahr);
	}

	public editWettbewerb(wettbewerb: Wettbewerb): void {
		this.store.dispatch(WettbewerbActions.startEditingWettbewerb({ wettbewerb: wettbewerb }))
		this.logger.debug('navigieren zum Editor für Jahr ' + wettbewerb.jahr);
		this.router.navigateByUrl('/wettbewerbe/wettbewerb-editor/' + wettbewerb.jahr);
	}


	public loadWettbewerbe(): Observable<Wettbewerb[]> {

		if (this.loggingOut) {
			return of([]);
		}

		const url = environment.apiUrl + '/wettbewerbe';

		this.logger.debug(url);

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public loadWettbewerbDetails(jahr: number): Observable<Wettbewerb> {

		if (this.loggingOut) {
			return of(undefined);
		}


		const url = environment.apiUrl + '/wettbewerbe/wettbewerb/' + jahr;

		this.logger.debug(url);

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}

	public saveWettbewerb(wettbewerb: WettbewerbEditorModel, neu: boolean): void {

		if (neu) {
			this.insertWettbewerb(wettbewerb);
		} else {
			this.updateWettbewerb(wettbewerb);
		}
	}

	private insertWettbewerb(wettbewerb: WettbewerbEditorModel): void {

		const url = environment.apiUrl + '/wettbewerbe/wettbewerb';

		this.http.post(url, wettbewerb).pipe(
			map(body => body as ResponsePayload),
		).subscribe(
			(responsePayload) => {
				this.messageService.info(responsePayload.message.message);
				this.store.dispatch(WettbewerbActions.wettbewerbInserted({ wettbewerb: wettbewerb, outcome: responsePayload.message }));
			},
			(error) => {
				const message: Message = {
					level: 'ERROR',
					message: this.errorMapper.extractMessageObject(error).message
				};
				this.store.dispatch(WettbewerbActions.saveFailed({ outcome: message }));
			}
		);
	}

	private updateWettbewerb(wettbewerb: WettbewerbEditorModel): void {

		const url = environment.apiUrl + '/wettbewerbe/wettbewerb';

		this.http.put(url, wettbewerb).pipe(
			map(body => body as ResponsePayload),
		).subscribe(
			(responsePayload) => {
				this.messageService.info(responsePayload.message.message);
				this.store.dispatch(WettbewerbActions.wettbewerbUpdated({ wettbewerb: wettbewerb, outcome: responsePayload.message }));
			},
			(error) => {
				const message: Message = {
					level: 'ERROR',
					message: this.errorMapper.extractMessageObject(error).message
				};
				this.store.dispatch(WettbewerbActions.saveFailed({ outcome: message }));
			}
		);
	}

	public moveWettbewerbOn(wettbewerb: Wettbewerb): void {

		const url = environment.apiUrl + '/wettbewerbe/wettbewerb/status';

		const payload = { jahr: wettbewerb.jahr };

		this.http.put(url, payload).pipe(
			map(body => body as ResponsePayload),
		).subscribe(
			(responsePayload) => {
				this.messageService.info(responsePayload.message.message);
				this.store.dispatch(WettbewerbActions.wettbewerbMovedOn({ wettbewerb: wettbewerb, neuerStatus: responsePayload.data, outcome: responsePayload.message }));
			},
			(error) => {
				const message: Message = {
					level: 'ERROR',
					message: this.errorMapper.extractMessageObject(error).message
				};
				this.store.dispatch(WettbewerbActions.saveFailed({ outcome: message }));
			}
		);
	}
};

