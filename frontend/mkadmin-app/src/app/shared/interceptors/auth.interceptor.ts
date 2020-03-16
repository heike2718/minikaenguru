import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { STORAGE_KEY_USER, User } from '../../auth/model/user.model';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

		if (environment.production) {
			return next.handle(req);
		}

		// da auf localhost das cookie nicht in den Browser gesetzt und folglich zurückgesendet werden kann,
		// machen wir hier den Umweg über localStorage.
		const userVal = localStorage.getItem(STORAGE_KEY_USER);
		if (userVal) {
			const user = JSON.parse(userVal) as User;
			const sessionId = user.sessionId;
			if (sessionId) {
				const cloned = req.clone({
					headers: req.headers.set('X-SESSIONID', sessionId)
				});
				return next.handle(cloned);

			}
		} else {
			return next.handle(req);
		}
	}
}
