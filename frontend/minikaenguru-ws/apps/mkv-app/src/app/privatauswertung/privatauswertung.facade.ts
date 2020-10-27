import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import * as PrivatauswertungSelectors from './+state/privatauswertung.selectors';
import * as PrivatauswertungActions from './+state/privatauswertung.actions';
import { Observable, of } from 'rxjs';
import { Kind, KindEditorModel } from '@minikaenguru-ws/common-components';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { PrivatauswertungService } from './privatauswertung.service';
import { take } from 'rxjs/operators';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';



@Injectable({
	providedIn: 'root'
})
export class PrivatauswertungFacade {

	public kindEditorModel$: Observable<KindEditorModel> = this.store.select(PrivatauswertungSelectors.kindEditorModel);
	public kinder$: Observable<Kind[]> = this.store.select(PrivatauswertungSelectors.kinder);
	public kinderGeladen$: Observable<boolean> = this.store.select(PrivatauswertungSelectors.kinderGeladen);


	private loggingOut: boolean;

	constructor(private store: Store<AppState>,
		private authService: AuthService,
		private privatauswertungService: PrivatauswertungService,
		private errorHandler: GlobalErrorHandlerService) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);
	}

	public createNewKind(): void {

		this.store.dispatch(PrivatauswertungActions.createNewKind());
		//TODO navigate to the Editor

	}


	public editKind(kind: Kind): void {

		this.store.dispatch(PrivatauswertungActions.startEditingKind({ kind: kind }));
		//TODO navigate to the Editor

	}

	public loadKinder(): void {

		if (this.loggingOut) {
			return;
		}

		this.store.dispatch(PrivatauswertungActions.startLoading());

		this.privatauswertungService.loadKinder().pipe(
			take(1)
		).subscribe(
			kinder => this.store.dispatch(PrivatauswertungActions.allKinderLoaded({kinder: kinder})),
			(error => {
				this.store.dispatch(PrivatauswertungActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}
};
