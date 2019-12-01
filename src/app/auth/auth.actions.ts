import { createAction, props } from '@ngrx/store';
import { User } from './model/user.model';


export const loadUser = createAction('[Top Menu] User Login', props<{user: User}>());

export const logOut = createAction('[Top Menu] Logout');

