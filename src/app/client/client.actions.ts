import { createAction, props } from '@ngrx/store';
import { Client, JWTPayload } from './model/client.model';

export const initAccessToken = createAction('[App Component] Init AccessToken', props<{ client: Client }>());

export const refereshAccessToken = createAction('[Top Menu]', props<{ client: Client }>());

export const initJWT = createAction('[App Component] Init JWT', props<{ jwt: JWTPayload }>());

export const refereshJWT = createAction('[Top Menu]', props<{ jwt: JWTPayload}>());

export const logout = createAction('[Top Menu] Logout');

export const deleteJWT = createAction('[App Component] Delete JTW');
