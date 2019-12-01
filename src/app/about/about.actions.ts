import { createAction, props } from '@ngrx/store';
import { AboutInfos } from './model/about.model';


export const loadAboutInfos = createAction(
	'[About Resolver] Load about infos'
);

export const aboutInfosLoaded = createAction(
	'[Load AboutInfos Effect] about infos loaded',
	props<{about: AboutInfos}>()
);
