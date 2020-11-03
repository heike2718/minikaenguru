import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../../reducers';
import { Observable } from 'rxjs';
import { kindEditorModel, kinderMap } from '../+state/kinder.selectors';
import { first, tap } from 'rxjs/operators';
import { KinderMap } from '../kinder.model';
import { Kind } from '@minikaenguru-ws/common-components';
import { KinderFacade } from '../kinder.facade';


@Injectable()
export class KindEditorResolver implements Resolve<any> {

	constructor(private store: Store<AppState>, private kinderFacade: KinderFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


			const uuid = route.params.id;

			if (uuid === 'neu') {

				return this.store.pipe(
					select(kindEditorModel),
					tap(
						_m => this.kinderFacade.createNewKind()
					),
					first()
				);
			} else {

				return this.store.pipe(
					select(kinderMap),
					tap(
						map => {
							const kind: Kind = new KinderMap(map).get(uuid);
							this.kinderFacade.editKind(kind);
						}
					),
					first()
				)
			}
	}
}
