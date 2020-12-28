import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';

import { MessageService } from '@minikaenguru-ws/common-messages';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { UrkundenService } from './urkunden.service';
import { Kind } from '@minikaenguru-ws/common-components';
import * as UrkundenActions from './+state/urkunden.actions';
import { UrkundenauftragEinzelkind } from './urkunden.model';
import { Router } from '@angular/router';


@Injectable({
	providedIn: 'root'
})
export class UrkundenFacade {

	constructor(private store: Store<AppState>,
		private urkundenService: UrkundenService,
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


	public resetState(): void {

		this.store.dispatch(UrkundenActions.resetModule());

	}

}
