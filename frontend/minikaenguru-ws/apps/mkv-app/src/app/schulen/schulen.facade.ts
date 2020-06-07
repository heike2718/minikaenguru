import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { allSchulen } from './+state/schulen.selectors';
import { SchulenService } from '../services/schulen.service';
import * as SchulenActions from './+state/schulen.actions';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';


@Injectable({ providedIn: 'root' })
export class SchulenFacade {

	public schulen$ = this.appStore.select(allSchulen);

	constructor(private appStore: Store<AppState>,
		private schulenService: SchulenService,
		private errorHandler: GlobalErrorHandlerService) {
	}

	public loadSchulen(): void {

		this.schulenService.findSchulen().subscribe(
			schulen => {
				this.appStore.dispatch(SchulenActions.schulenLoaded({ schulen: schulen }));
			},
			(error => {
				this.errorHandler.handleError(error)
			})
		);
	}

	public resetState(): void {

		this.appStore.dispatch(SchulenActions.resetSchulen());
	}
}
