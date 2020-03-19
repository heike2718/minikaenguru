export type MessageLevel = 'INFO' | 'WARN' | 'ERROR';

export interface Message {
    readonly level: MessageLevel;
    readonly message: string;
}

export interface ResponsePayload {
    readonly message: Message;
    readonly data?: any;
}

