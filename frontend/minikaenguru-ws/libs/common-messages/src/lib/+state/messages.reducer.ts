import { Action, createReducer, on } from '@ngrx/store';
import { Message } from "@minikaenguru-ws/common-messages";
import * as MessagesActions from './messages.actions';

export const messagesFeatureKey = 'messages';

export interface MessagesState {
   readonly message?: Message;
};

const initialMessagesState: MessagesState = {
    message: undefined
};

const messagesReducer = createReducer(initialMessagesState,
    
    on(MessagesActions.showInfo, (state, action) => {

        const message: Message = {level: 'INFO', message: action.message};
        return {...state, message: message};
    }),

    on(MessagesActions.showWarning, (state, action) => {

        const message: Message = {level: 'WARN', message: action.message};
        return {...state, message: message};
    }),

    on(MessagesActions.showEror, (state, action) => {

        const message: Message = {level: 'ERROR', message: action.message};
        return {...state, message: message};
    }),

    on(MessagesActions.showMessage, (state, action) => {

       return {...state, message: action.message};
    }),

    on(MessagesActions.clearMessage, (state, _action) => {
        return initialMessagesState;
    })
);

export function reducer(state: MessagesState | undefined, action: Action) {
	return messagesReducer(state, action);
}

