
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { Loesungszettel, createLoseungszettelzeilen, LoesungszettelMap } from './loesungszettel.model';
import { Klassenstufe, Kind, LoesungszettelResponse, Loesungszettelzeile } from '@minikaenguru-ws/common-components';
import * as LoesungszettelActions from './+state/loesungszettel.actions';
import * as LoesungszettelSelectors from './+state/loesungszettel.selectors';
import { LoesungszettelService } from './loesungszettel.service';
import { Subscription, Observable } from 'rxjs';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as KinderActions from '../kinder/+state/kinder.actions';
import * as KlassenActons from '../klassen/+state/klassen.actions';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelFacade {

	public selectedLoesungszettel$: Observable<Loesungszettel | undefined> = this.store.select(LoesungszettelSelectors.selectedLoesungszettel);


	private loesungszettelMap!: LoesungszettelMap;

	constructor(private store: Store<AppState>,
		private loesungszettelService: LoesungszettelService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService,) {

		this.store.select(LoesungszettelSelectors.loesungszettelMap).subscribe(

			map => this.loesungszettelMap = map
		);
	}


	public createNewLoesungszettel(kind: Kind): void {

		const zeilen: Loesungszettelzeile[] = createLoseungszettelzeilen(kind.klassenstufe.klassenstufe);

		const loesungszettel: Loesungszettel = {
			uuid: 'neu',
			kindID: kind.uuid,
			klassenstufe: kind.klassenstufe.klassenstufe,
			zeilen: zeilen,
			version: -1
		};

		this.store.dispatch(LoesungszettelActions.newLoesungszettelCreated({ loesungszettel: loesungszettel }));
		this.selectLoesungszettel(loesungszettel);
	}

	public loadLoesungszettel(kind: Kind): void {

		if (kind.punkte) {
			if (!this.loesungszettelMap.has(kind.punkte.loesungszettelId)) {

				this.store.dispatch(LoesungszettelActions.startLoading());

				this.loesungszettelService.loadLoesungszettelWithID(kind).subscribe(

					zettel => {
						this.store.dispatch(LoesungszettelActions.loesungszettelLoaded({ loesungszettel: zettel }));
						this.selectLoesungszettel(zettel);
					},
					(error => {
						this.store.dispatch(LoesungszettelActions.finishedWithError());
						this.errorHandler.handleError(error);
					})

				);
			} else {
				const zettel: Loesungszettel | undefined = this.loesungszettelMap.get(kind.punkte.loesungszettelId);
				if (zettel) {
					this.store.dispatch(LoesungszettelActions.loesungszettelLoaded({ loesungszettel: zettel }));
					this.selectLoesungszettel(zettel);
				}				
			}
		}
	}

	public saveLoesungszettel(kind: Kind, loesungszettel: Loesungszettel): void {

		this.store.dispatch(LoesungszettelActions.startLoading());

		this.loesungszettelService.saveLoesungszettel(loesungszettel).subscribe(

			responsePayload => {

				const data = responsePayload.data;

				if (data) {

					const loesungszettelResponse: LoesungszettelResponse = responsePayload.data;
					this.store.dispatch(LoesungszettelActions.loesungszettelSaved({ loesungszettelAlt: loesungszettel, loesungszettelNeu: loesungszettelResponse }));
					this.store.dispatch(KinderActions.kindLoesungszettelChanged({ kind: kind, loesungszettelResponse: loesungszettelResponse }));

					if (kind.klasseId && loesungszettel.uuid === 'neu') {
						this.store.dispatch(KlassenActons.loesungszettelAdded({ kind: kind }));
					}
				} else {
					this.handleLoesungszettelDeleted(loesungszettel,kind);
				}


				this.messageService.showMessage(responsePayload.message);
			},
			(error => {
				this.store.dispatch(LoesungszettelActions.finishedWithError());
				this.errorHandler.handleError(error);
			})

		);

	}

	public deleteLoesungszettel(kind: Kind, loesungszettel: Loesungszettel): void {

		if (!loesungszettel || !loesungszettel.uuid || loesungszettel.uuid === 'neu') {
			this.store.dispatch(KinderActions.unselectKind());
		}

		this.store.dispatch(LoesungszettelActions.startLoading());

		this.loesungszettelService.deleteLoesungszettel(loesungszettel.uuid).subscribe(

			responsePayload => {

				this.handleLoesungszettelDeleted(loesungszettel, kind);
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
		this.store.dispatch(KinderActions.unselectKind());
	}

	public resetState(): void {
		this.store.dispatch(LoesungszettelActions.resetModule());
	}

	public loesungszettelChanged(zeile: Loesungszettelzeile): void {

		this.store.dispatch(LoesungszettelActions.loesungszettelChanged({ zeile: zeile }));
	}

	// //////

	private handleLoesungszettelDeleted(loesungszettel: Loesungszettel, kind: Kind) {
		this.store.dispatch(LoesungszettelActions.loesungszettelDeleted({ loesungszettel: loesungszettel }));
		this.store.dispatch(KinderActions.kindLoesungszettelDeleted({ kindID: loesungszettel.kindID }));
		this.store.dispatch(KinderActions.unselectKind());

		if (kind.klasseId) {
			this.store.dispatch(KlassenActons.loesungszettelDeleted({ kind: kind }));
		}
	}
}

