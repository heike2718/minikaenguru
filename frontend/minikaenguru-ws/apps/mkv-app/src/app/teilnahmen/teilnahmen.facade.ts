import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { loading, anonymisierteTeilnahmenGeladen, anonymisierteTeilnahmen, selectTeilnahmenummerAndName } from './+state/teilnahmen.selectors';
import * as TeilnahmenActions from './+state/teilnahmen.actions';
import { tap, finalize, take } from 'rxjs/operators';
import { LehrerFacade } from '../lehrer/lehrer.facade';
import { PrivatveranstalterFacade } from '../privatveranstalter/privatveranstalter.facade';
import { AuthService, User } from '@minikaenguru-ws/common-auth';
import { forkJoin, ObjectUnsubscribedError, Subscription } from 'rxjs';
import { Router } from '@angular/router';


@Injectable({ providedIn: 'root' })
export class TeilnahmenFacade {

	public teilnahmenummerAndName$ = this.appStore.select(selectTeilnahmenummerAndName);
	public loading$ = this.appStore.select(loading);
	public anonymisierteTeilnahmenGeladen$ = this.appStore.select(anonymisierteTeilnahmenGeladen);
	public anonymisierteTeilnahmen$ = this.appStore.select(anonymisierteTeilnahmen);

	constructor(private appStore: Store<AppState>,
		private authService: AuthService,
		private teilnahmenService: TeilnahmenService,
		private lehrerFacade: LehrerFacade,
		private privatveranstalterFacade: PrivatveranstalterFacade,
		private router: Router,
		private errorHandler: GlobalErrorHandlerService) { }

	public selectTeilnahmenummer(nummer: string, name: string): void {

		this.appStore.dispatch(TeilnahmenActions.teilnahmenummerSelected({ teilnahmenummer: nummer, nameSchule: name }));

	}

	public initTeilnahmen(rolle: string): void {

		if (rolle === 'PRIVAT') {
			this.initPrivatteilnahmen();
		}
		if (rolle === 'LEHRER') {
			this.initLehrerteilnahmen();
		}
	}

	private initPrivatteilnahmen() {

		this.privatveranstalterFacade.veranstalter$.pipe(
			take(1),
			tap(
				veranstalter => {
					if (!veranstalter) {
						this.router.navigateByUrl('/privat/dashboard')
					} else {
						this.internalLoadTeilnahmen(veranstalter.teilnahmenummer);
					}
				}
			)
		).subscribe();

	}

	private initLehrerteilnahmen() {

		this.lehrerFacade.selectedSchule$.pipe(

			tap(
				schule => {
					if (!schule) {
						this.router.navigateByUrl('/lehrer/schulen');
					} else {
						this.internalLoadTeilnahmen(schule.kuerzel);
					}
				}
			)

		).subscribe();
	}


	private internalLoadTeilnahmen(nummer: string) {
		this.teilnahmenService.ladeAnonymisierteTeilnahmen(nummer).subscribe(

			teilnahmen => {
				this.appStore.dispatch(TeilnahmenActions.anonymeTeilnahmenGeladen({ anonymeTeilnahmen: teilnahmen }));
			},
			(error => {
				this.appStore.dispatch(TeilnahmenActions.loadFinishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public resetState(): void {
		this.appStore.dispatch(TeilnahmenActions.resetState());
	}

}

