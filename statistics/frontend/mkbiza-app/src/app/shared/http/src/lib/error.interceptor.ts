import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { MessageService } from '@mkbiza-app/messages-api';
import { catchError, Observable, throwError } from 'rxjs';
import { ERROR_MESSAGE_CONTEXT } from './http.context';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  #messageService = inject(MessageService);

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((err) => {        
        const errorMessageContext = req.context.get(ERROR_MESSAGE_CONTEXT);
        this.#messageService.error(errorMessageContext);
        // err['message'] = errorMessageContext;
        return throwError(() => err);
      })
    );
  }
}
