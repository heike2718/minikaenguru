import { createAction, props } from '@ngrx/store';
import { Client } from './model/client.model';

export const initAccessToken = createAction('[App Component] Init AccessToken', props<{ client: Client }>());

export const refereshAccessToken = createAction('[Top Menu]', props<{ client: Client }>());

