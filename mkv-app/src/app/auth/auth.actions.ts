import { createAction, props } from '@ngrx/store';
import { Session } from 'mk-ng-commons';


export const createSession = createAction(
    '[Auth Component] Session created',
    props<{ session: Session }>()
);




