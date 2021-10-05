import { Message } from '@minikaenguru-ws/common-messages';
import { createAction, props } from '@ngrx/store';


export const showInfo = createAction (
    '[MessageService] info',
    props<{message: string}>()
);

export const showWarning = createAction (
    '[MessageService] warn',
    props<{message: string}>()
);

export const showEror = createAction (
    '[MessageService] error',
    props<{message: string}>()
);

export const showMessage = createAction (
    '[MessageService] showMessage',
    props<{message: Message}>()
);

export const clearMessage = createAction (
    '[MessageService] clear'
);


