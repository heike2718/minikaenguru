import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../reducers';
import { Observable } from 'rxjs';
import { wettbewerbEditorModel, selectedWettbewerb } from './+state/wettbewerbe.selectors';
import { tap, filter, first } from 'rxjs/operators';
import { loadWettbewerbDetails } from './+state/wettbewerbe.actions';
import { initialWettbewerbEditorModel } from './wettbewerbe.model';

@Injectable()
export class WettbewerbDetailsResolver implements Resolve<any> {

	constructor(private store: Store<AppState>) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


		const id = route.params.id;
		let jahr: number | null;
		if (id === 'neu') {
			jahr = initialWettbewerbEditorModel.jahr;

			return this.store.pipe(
				select(wettbewerbEditorModel),
				filter(editorModel => editorModel !== undefined && editorModel.jahr === jahr),
				first()
			);
		} else {
			try {
				jahr = parseInt(route.params.id, 0);
			} catch {
				jahr = null;
			}

			return this.store.pipe(
				select(selectedWettbewerb),
				tap(w => {
					if (jahr !== null && w && !w.teilnahmenuebersicht) {
						this.store.dispatch(loadWettbewerbDetails({ jahr: jahr }))
					}
				}),
				filter(wettbewerb => wettbewerb !== undefined && wettbewerb.jahr === jahr),
				first()
			);
		}
	}
}
