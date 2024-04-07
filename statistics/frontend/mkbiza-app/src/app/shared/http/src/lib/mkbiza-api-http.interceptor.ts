import {
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Configuration } from '@mkbiza-app/config';
import { UUID } from 'angular2-uuid';

/**
 * Packt eine correlationId und die clientId in den Request
 */
@Injectable()
export class MkbizaAPIHttpInterceptor implements HttpInterceptor {

  #config = inject(Configuration);

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {

    const url = this.#config.baseUrl + req.url;

    console.log(url);

    const correlationId = this.#generateUUID();

    const headers: HttpHeaders = req.headers.append('X-CLIENT-ID', this.#config.clientId).append('X-CORRELATION-ID', correlationId);

    return next.handle(
      req.clone({
        headers: headers,
        url: url
      })
    );
  }

  #generateUUID(): string {

    return UUID.UUID();
  }
}
