import { createAction, props } from '@ngrx/store';
import { User } from './model/user.model';


export const loadUser = createAction('[Top Menu] User Login', props<{user: User}>());

export const removeUser = createAction('[Top Menu] Logout');

