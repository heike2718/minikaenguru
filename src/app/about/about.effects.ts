import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { AboutActions } from './about-action-types';
import { concatMap, map } from 'rxjs/operators';
import { AboutService } from './about.service';
import { aboutInfosLoaded } from './about.actions';


@Injectable()
export class AboutEffects {

	// muss in app.module.ts deklariert werden

	loadAboutInfos$ = createEffect(
		() => this.actions$.pipe(
			ofType(AboutActions.loadAboutInfos),
			concatMap(_action => this.aboutService.loadAboutInfos()),
			map(payload => aboutInfosLoaded({
				about: {
					apiVersion: payload.message.message
				}
			}))
		)
	);

	constructor(private actions$: Actions, private aboutService: AboutService) {

	}

}
