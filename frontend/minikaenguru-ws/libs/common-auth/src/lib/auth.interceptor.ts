import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Inject, Injectable } from '@angular/core';
import { MkAuthConfigService, MkAuthConfig } from './configuration/mk-auth-config';
import { STORAGE_KEY_DEV_SESSION_ID } from './domain/entities';


@Injectable({
	providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {

	constructor(@Inject(MkAuthConfigService) private config: MkAuthConfig) {}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

		if (this.config.production) {
			const cloned = req.clone({
				headers: req.headers.set('Cache-Control', 'no-cache')
			});
			return next.handle(cloned);
		}

		// da auf localhost das cookie nicht in den Browser gesetzt und folglich zurückgesendet werden kann,
		// machen wir hier den Umweg über localstorage.
		const sessionId = localStorage.getItem(this.config.storagePrefix + STORAGE_KEY_DEV_SESSION_ID);
		if (sessionId) {
			let cloned = req.clone({
				headers: req.headers.set('X-SESSIONID', sessionId)
			});
			cloned = cloned.clone({
				headers: req.headers.set('Cache-Control', 'no-cache')
			});
			return next.handle(cloned);

		} else {
			return next.handle(req);
		}
	}
}
