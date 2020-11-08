import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../../reducers';
import { Observable } from 'rxjs';
import { first, tap } from 'rxjs/operators';
import { Klasse } from '@minikaenguru-ws/common-components';
import { KlassenFacade } from '../klassen.facade';
import { klasseEditorModel, klassenMap} from '../+state/klassen.selectors';
import { KlassenMap } from '../klassen.model';


@Injectable()
export class KlasseEditorResolver implements Resolve<any> {

	constructor(private store: Store<AppState>, private klassenFacade: KlassenFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


			const uuid = route.params.id;

			if (uuid === 'neu') {

				return this.store.pipe(
					select(klasseEditorModel),
					tap(
						m => {
							if (m === undefined) {
								this.klassenFacade.createNewKlasse();
							}
						}
					),
					first()
				);
			} else {

				return this.store.pipe(
					select(klassenMap),
					tap(
						map => {
							const klasse: Klasse = new KlassenMap(map).get(uuid);
							this.klassenFacade.editKlasse(klasse);
						}
					),
					first()
				)
			}
	}
}
