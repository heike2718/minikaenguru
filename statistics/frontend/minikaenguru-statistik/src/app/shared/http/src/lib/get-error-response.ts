import { HttpErrorResponse } from "@angular/common/http";


export function getHttpErrorResponse(error: NonNullable<unknown>): HttpErrorResponse | undefined {

    if (error instanceof HttpErrorResponse) {
        return <HttpErrorResponse>error;
    }

    return undefined;

}