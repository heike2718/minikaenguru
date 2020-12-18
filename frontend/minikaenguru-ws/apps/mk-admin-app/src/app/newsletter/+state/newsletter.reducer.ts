import { Action, createReducer, on } from '@ngrx/store';
import * as NewsletterActions from './newsletter.actions';
import { NewsletterWithID, Newsletter, NewsletterMap } from '../newsletter.model';

export const newsletterFeatureKey = 'mk-admin-app-newsletters';

export interface NewslettersState {
	readonly newsletterMap: NewsletterWithID[];
	readonly selectedNewsletter: Newsletter;
	readonly newslettersLoaded: boolean;
	readonly loading: boolean;

};

const initialNewsletterState: NewslettersState = {
	newsletterMap: [],
	selectedNewsletter: undefined,
	loading: false,
	newslettersLoaded: false
};

const newsletterReducer = createReducer(initialNewsletterState,

	on(NewsletterActions.loadNewsletters, (state, action) => {

		const newMap: NewsletterWithID[] = [];
		action.newsletters.forEach(n => newMap.push({ uuid: n.uuid, newsletter: n }));

		return { ...state, newslettersLoaded: true, loading: false, newsletterMap: newMap };
	})
);

export function reducer(state: NewslettersState | undefined, action: Action) {
	return newsletterReducer(state, action);
};


