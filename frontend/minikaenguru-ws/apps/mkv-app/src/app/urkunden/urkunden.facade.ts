import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { environment } from '../../environments/environment';

import { MessageService } from '@minikaenguru-ws/common-messages';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { UrkundenService } from './urkunden.service';
import { Kind, DownloadButtonModel, DownloadFacade } from '@minikaenguru-ws/common-components';
import * as UrkundenActions from './+state/urkunden.actions';
import * as UrkundenSelecors from './+state/urkunden.selectors';
import { UrkundenauftragEinzelkind } from './urkunden.model';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';


@Injectable({
	providedIn: 'root'
})
export class UrkundenFacade {

	public loading$: Observable<boolean> = this.store.select(UrkundenSelecors.loading);
	public urkundenauftrag$: Observable<UrkundenauftragEinzelkind> = this.store.select(UrkundenSelecors.urkundenauftrag);

	constructor(private store: Store<AppState>,
		private urkundenService: UrkundenService,
		private downloadFacade: DownloadFacade,
		private router: Router,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService,) { }


	public createUrkundenauftrag(kind: Kind): void {

		const auftrag: UrkundenauftragEinzelkind = {
			kindUuid: kind.uuid,
			urkundenart: 'TEILNAHME'
		};

		this.store.dispatch(UrkundenActions.initialerUrkundenauftragCreated({auftrag: auftrag}));
		this.router.navigateByUrl('/urkunden');
	}

	public downloadUrkunde(auftrag: UrkundenauftragEinzelkind): void {

		this.store.dispatch(UrkundenActions.startLoading());

		// const auftrag: UrkundenauftragEinzelkind = {
		// 	kindUuid: 'e0f2467a-6b19-4b37-9d38-0dfc3d327faf',
		// 	dateString: '29.12.2020',
		// 	farbschema: 'ORANGE',
		// 	urkundenart: 'TEILNAHME'
		// };

		const defaultFilename = 'teilnahmeurkunde_e0f2467a.pdf';


		this.urkundenService.generateUrkunde(auftrag).pipe(
			take(1)
		).subscribe(
			blob => {
				this.downloadFacade.saveAs(blob, defaultFilename);
				this.store.dispatch(UrkundenActions.downloadFinished());
			},
			(error => {
				this.store.dispatch(UrkundenActions.downloadFinished());
				this.errorHandler.handleError(error);
			})
		);


	}


	public resetState(): void {

		this.store.dispatch(UrkundenActions.resetModule());

	}


	public createDownloadUrkundeButtonModel(dateString: string): DownloadButtonModel {

		const result: DownloadButtonModel = {
			id: 'eventlog-today',
			url: environment.apiUrl + '/veranstalter/urkunden/urkunde',
			buttonLabel: 'ab ' + dateString,
			dateiname: dateString + '-mkv-gateway-events.log',
			mimetype: 'application/octet-stream',
			tooltip: 'eventlog ab ' + dateString + ' herunterladen',
			class: 'btn btn-outline-dark w-100 ml-1'
		};

		return result;

	}

}
