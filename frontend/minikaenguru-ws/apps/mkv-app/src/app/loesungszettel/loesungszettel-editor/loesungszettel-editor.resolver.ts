import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { of, Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import * as KinderSelectors from '../../kinder/+state/kinder.selectors';
import { tap, first } from 'rxjs/operators';
import { LoesungszettelFacade } from '../loesungszettel.facade';



@Injectable()
export class LoesungszettelEditorResolver implements Resolve<any> {

	constructor(private store: Store<AppState>, private loesungszettelFacade: LoesungszettelFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


			return this.store.select(KinderSelectors.selectedKind).pipe(
				tap(
					kind => {

						if (kind) {
							if (kind.loesungszettelId) {
								this.loesungszettelFacade.loadLoesungszettel(kind)
							} else {
								this.loesungszettelFacade.createNewLoesungszettel(kind);
							}
						}
					}
				),
				first()
			);
	}
}
