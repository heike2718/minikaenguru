import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as PrivatveranstalterActions from './+state/privatveranstalter.actions';


@Injectable({ providedIn: 'root' })
export class PrivatveranstalterFacade {

	constructor(private appStore: Store<AppState>,
		private errorHandler: GlobalErrorHandlerService) { }


	public resetState(): void {

		this.appStore.dispatch(PrivatveranstalterActions.resetPrivatveranstalter());

	}

}
