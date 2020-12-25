import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of, throwError } from 'rxjs';
import { Loesungszettel, Loesungszettelzeile, createLoseungszettelzeilen } from './loesungszettel.model';
import { Kind, Klassenstufe, LoesungszettelPunkte } from '@minikaenguru-ws/common-components';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { env } from 'process';
import { map } from 'rxjs/operators';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelService {

	constructor(private http: HttpClient) { }

	public loadLoesungszettelWithID(kind: Kind): Observable<Loesungszettel> {

		if (!kind.punkte) {
			return throwError("invalid ID");
		}

		const url = environment.apiUrl + '/veranstalter/loesungszettel/' + kind.punkte.loesungszettelId;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}


	public saveLoesungszettel(loesungszettel: Loesungszettel): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/veranstalter/loesungszettel';

		if (loesungszettel.uuid === 'neu') {
			return this.insertLoesungszettel(url, loesungszettel);
		}

		return this.uppdateLoesungszettel(url, loesungszettel);
	}

	public deleteLoesungszettel(loesungszettelID: string): Observable<ResponsePayload> {
		const url = environment.apiUrl + '/veranstalter/loesungszettel/' + loesungszettelID;

		return this.http.delete(url).pipe(
			map(body => body as ResponsePayload)
		);
	}

	// /////////////////////////////

	private insertLoesungszettel(url: string, loesungszettel: Loesungszettel): Observable<ResponsePayload> {

		return this.http.post(url, loesungszettel).pipe(
			map(body => body as ResponsePayload)
		);

	}

	private uppdateLoesungszettel(url: string, loesungszettel: Loesungszettel): Observable<ResponsePayload> {

		return this.http.put(url, loesungszettel).pipe(
			map(body => body as ResponsePayload)
		);

	}

}
