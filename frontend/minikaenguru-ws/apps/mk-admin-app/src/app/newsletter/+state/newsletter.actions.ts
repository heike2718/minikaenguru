import { createAction, props } from '@ngrx/store';
import { Newsletter } from '../newsletter.model';


export const loadNewsletters = createAction(
	'[NewsletterFacade]: loadNewsletters',
	props<{newsletters: Newsletter[]}>()
);
