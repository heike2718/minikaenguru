import { HttpErrorResponse } from "@angular/common/http";
import { Message } from "@mks/messages-api";
import { ConstraintViolation } from "./http.context";


export function extractServerErrorMessage(error: HttpErrorResponse): Message {

    if (error.status === 0) {
        return { level: 'ERROR', message: 'Der Server ist nicht erreichbar.' };
    }

    const errorResponse: HttpErrorResponse = <HttpErrorResponse>error;

    if (window.location.hash.indexOf('idToken') >= 0) {
        window.location.hash = '';
    }

    if (errorResponse.status === 400) {
        const error = errorResponse.error;

        if (error['violations']) {

            const violations: ConstraintViolation[] = <ConstraintViolation[]>error['violations'];

            let message = '';
            violations.forEach(v => {
                const index = v.field.indexOf('.');
                const field = v.field.substring(index + 1);
                message += field + ': ' + v.message;
            });

            return { level: 'ERROR', message: message };
        } else {
            const payload: Message = errorResponse.error;

            if (payload) {
                return payload;
            }
        }
    } else {
        const payload: Message = errorResponse.error;

        if (payload && payload.level && payload.message) {
            return {message: payload.message, level: payload.level};
        }
    }

    return getGenericMessageForStatus(error.status);
}


function getGenericMessageForStatus(status: number): Message {

    let result = '';

    switch (status) {
        case 400: result = 'Payload oder Queryparameter haben Fehler'; break;
        case 401: result = 'keine Berechtigung (401)'; break;
        case 403: result = 'unerlaubte Aktion (403)'; break;
        case 404: result = 'Das Objekt existiert nicht (404)'; break;
        case 409: result = 'Speichern wegen eines Datenkonflikts nicht möglich'; break;
        default: result = 'Im Backend ist ein unerwarteter Fehler aufgetreten.';
    }

    return { level: 'ERROR', message: result };
}