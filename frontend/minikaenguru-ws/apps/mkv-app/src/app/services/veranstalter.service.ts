import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map, tap } from 'rxjs/operators';


@Injectable({
	providedIn: 'root'
})
export class VeranstalterService {

	constructor(private http: HttpClient) { }

	findTeilnahmenummern(): Observable<string[]> {

		console.log('[VeranstalterService] findTeilnahmenummern - Start')

		const url = environment.apiUrl + '/wettbewerb/teilnahmen/teilnahmenummern';

		console.log('[VeranstalterService] findTeilnahmenummern - url = ' + url)

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			tap(payload => console.log('[VeranstalterService]: ' + payload.message.message)),
			map(payload => payload.data)
		)
	}

}
