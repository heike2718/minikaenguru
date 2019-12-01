import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { tap, first, finalize } from 'rxjs/operators';
import { loadAboutInfos } from './about.actions';

@Injectable()
export class AboutResolver implements Resolve<any> {

	loading = false;

	// router reslover sorgt dafür, dass zuerst alle Daten vom Backend geladen werden, bevor die Component angezeigt wird.
	// muss in app.module.ts und app-routing.modile.ts deklariert werden (oder im passenden Untermodul)

	constructor(private store: Store<AppState>) { }

	resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {


		// tap is the recommended operator for side effects
		// loading kontrolliert, ob die action dispached wird. Anderenfalls werden zu viele backend calls getriggert
		return this.store.pipe(
			tap(() => {

				if (!this.loading) {
					this.loading = true;
					this.store.dispatch(loadAboutInfos());
				}

			}),
			first(),
			finalize(() => this.loading = false)
		);
	}

}

