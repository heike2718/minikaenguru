
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { Loesungszettel, Loesungszettelzeile, createLoseungszettelzeilen, LoesungszettelMap, LoesungszettelResponseModel } from './loesungszettel.model';
import { Klassenstufe, Kind } from '@minikaenguru-ws/common-components';
import * as LoesungszettelActions from './+state/loesungszettel.actions';
import * as LoesungszettelSelectors from './+state/loesungszettel.selectors';
import { LoesungszettelService } from './loesungszettel.service';
import { Subscription, Observable } from 'rxjs';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as KinderActions from '../kinder/+state/kinder.actions';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelFacade {

	public selectedLoesungszettel$: Observable<Loesungszettel> = this.store.select(LoesungszettelSelectors.selectedLoesungszettel);


	private loesungszettelMapSubscription: Subscription;

	private loesungszettelMap: LoesungszettelMap;

	constructor(private store: Store<AppState>,
		private loesungszettelService: LoesungszettelService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService,) {

		this.loesungszettelMapSubscription = this.store.select(LoesungszettelSelectors.loesungszettelMap).subscribe(

			map => this.loesungszettelMap = map
		);
	}


	public createNewLoesungszettel(kind: Kind): void {

		const zeilen: Loesungszettelzeile[] = createLoseungszettelzeilen(kind.klassenstufe.klassenstufe);

		const loesungszettel: Loesungszettel = {
			uuid: 'neu',
			kindID: kind.uuid,
			klassenstufe: kind.klassenstufe.klassenstufe,
			zeilen: zeilen
		};

		this.store.dispatch(LoesungszettelActions.createNewLoesungsettel({ loesungszettel: loesungszettel }));
		this.selectLoesungszettel(loesungszettel);
	}

	public loadLoesungszettel(kind: Kind): void {

		if (kind.loesungszettelPunkte) {
			if (!this.loesungszettelMap.has(kind.loesungszettelPunkte.loesungszettelId)) {

				this.store.dispatch(LoesungszettelActions.startLoading());

				this.loesungszettelService.loadLoesungszettelWithID(kind).subscribe(

					zettelResponse => {
						this.store.dispatch(LoesungszettelActions.loesungszettelLoaded({ loesungszettel: zettelResponse.loesungszettel }));
						this.selectLoesungszettel(zettelResponse.loesungszettel);
					},
					(error => {
						this.store.dispatch(LoesungszettelActions.finishedWithError());
						this.errorHandler.handleError(error);
					})

				);
			}
		}
	}

	public saveLoesungszettel(kind: Kind, loesungszettel: Loesungszettel): void {

		this.store.dispatch(LoesungszettelActions.startLoading());

		this.loesungszettelService.saveLoesungszettel(loesungszettel).subscribe(

			responsePayload => {

				const loesungszettelResponse: LoesungszettelResponseModel = responsePayload.data;
				this.store.dispatch(KinderActions.kindLoesungszettelChanged({kind: kind, punkte: loesungszettelResponse.punkte}));


				this.store.dispatch(LoesungszettelActions.loesungszettelSaved({ loesungszettel: loesungszettelResponse.loesungszettel }));
				this.selectLoesungszettel(loesungszettelResponse.loesungszettel);

				this.messageService.showMessage(responsePayload.message);
			},
			(error => {
				this.store.dispatch(LoesungszettelActions.finishedWithError());
				this.errorHandler.handleError(error);
			})

		);

	}

	public selectLoesungszettel(loesungszettel: Loesungszettel): void {
		this.store.dispatch(LoesungszettelActions.loesungszettelSelected({ loesungszettel: loesungszettel }));
	}

	public cancelEditLoesungszettel(): void {
		this.store.dispatch(LoesungszettelActions.editLoesungszettelCancelled());
	}

	public resetState(): void {
		this.store.dispatch(LoesungszettelActions.resetModule());
	}

	public loesungszettelChanged(zeile: Loesungszettelzeile): void {

		this.store.dispatch(LoesungszettelActions.loesungszettelChanged({ zeile: zeile }));
	}
}

