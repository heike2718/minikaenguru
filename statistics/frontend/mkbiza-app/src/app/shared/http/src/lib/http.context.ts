import { HttpContextToken } from '@angular/common/http';

const defaultErrorMessage = 'Ups, da ist leider ein Fehler aufgetreten.';

export const OPEN_API_HTTP_TOKEN = new HttpContextToken<boolean>(() => false);

/** kann als key in einem key-value pair mittels withErrorMessageContext() an einen HttpRequest mitgegeben werden, 
 * um eine kontextbezogene Fehlermeldung zu erzeugen.
 */
export const ERROR_MESSAGE_CONTEXT = new HttpContextToken(() => defaultErrorMessage);

export interface ConstraintViolation {
    readonly field: string;
    readonly message: string;
};