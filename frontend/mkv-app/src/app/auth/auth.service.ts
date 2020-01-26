import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { ResponsePayload, AuthResult } from 'hewi-ng-lib';
import { map } from 'rxjs/operators';
import { HttpErrorHandlerService } from '../error/http-error-handler.service';
import { Observable } from 'rxjs';
import { Session } from 'mk-ng-commons';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    constructor(private httpClient: HttpClient, private errorHandler: HttpErrorHandlerService) { }


    startLogin() {

        const url = environment.apiUrl + '/auth/login';

        this.httpClient.get(url).pipe(
            map(res => res as ResponsePayload)
        ).subscribe(
            payload => {
                window.location.href = payload.message.message;
            },
            (error => {
                this.errorHandler.handleError(error, 'logIn');
            }));
    }


    createSession(authResult: AuthResult): Observable<Session> {

        window.location.hash = '';

        const url = environment.apiUrl + '/auth/session';

        return this.httpClient.post(url, authResult.idToken).pipe(
            map((responsePayload: ResponsePayload) => responsePayload.data as Session));
    }
}
