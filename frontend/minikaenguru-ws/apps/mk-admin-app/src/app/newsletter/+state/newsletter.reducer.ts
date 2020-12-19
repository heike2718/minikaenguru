import { Action, createReducer, on } from '@ngrx/store';
import * as NewsletterActions from './newsletter.actions';
import { NewsletterWithID, Newsletter, NewsletterMap, initialNewsletterEditorModel, Versandinfo } from '../newsletter.model';

export const newsletterFeatureKey = 'mk-admin-app-newsletters';

export interface NewslettersState {
	readonly newsletterMap: NewsletterWithID[];
	readonly selectedNewsletter: Newsletter;
	readonly newsletterEditorModel: Newsletter;
	readonly versandinfo: Versandinfo;
	readonly newslettersLoaded: boolean;
	readonly loading: boolean;

};

const initialNewsletterState: NewslettersState = {
	newsletterMap: [],
	selectedNewsletter: undefined,
	newsletterEditorModel: undefined,
	versandinfo: undefined,
	loading: false,
	newslettersLoaded: false
};

const newsletterReducer = createReducer(initialNewsletterState,

	on(NewsletterActions.newslettersLoaded, (state, action) => {

		const newMap: NewsletterWithID[] = [];
		action.newsletters.forEach(n => newMap.push({ uuid: n.uuid, newsletter: n }));

		return { ...state, newslettersLoaded: true, loading: false, newsletterMap: newMap };
	}),

	on(NewsletterActions.createNewNewsletter, (state, _action) => {

		return { ...state, newsletterEditorModel: initialNewsletterEditorModel };

	}),

	on(NewsletterActions.edidCanceled, (state, _action) => {

		return {...state, newsletterEditorModel: undefined, loading: false };

	}),

	on(NewsletterActions.newsletterSaved, (state, action) => {

		const neueMap = new NewsletterMap(state.newsletterMap).merge(action.newsletter);

		return {...state, loading: false, newsletterMap: neueMap};

	}),

	on(NewsletterActions.mailversandScheduled, (state, action) => {

		return {...state, loading: false, versandinfo: action.versandinfo};
	}),

	on(NewsletterActions.editNewsletterTriggered, (state, action) => {

		return {...state, newsletterEditorModel: {...action.newsletter}, selectedNewsletter: {...action.newsletter}};

	}),

	on(NewsletterActions.resetNewsletters, (_state, _action) => {

		return initialNewsletterState;

	}),

	on(NewsletterActions.newsletterRemoved, (state, action) => {

		const neueMap = new NewsletterMap(state.newsletterMap).remove(action.newsletter);

		return {...state, loading: false, newsletterMap: neueMap, selectedNewsletter: undefined, newsletterEditorModel: undefined};

	}),

);

export function reducer(state: NewslettersState | undefined, action: Action) {
	return newsletterReducer(state, action);
};


