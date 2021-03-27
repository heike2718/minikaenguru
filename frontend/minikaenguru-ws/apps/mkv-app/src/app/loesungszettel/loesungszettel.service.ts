import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { Loesungszettel} from './loesungszettel.model';
import { Kind, Klassenstufe, LoesungszettelResponse, Loesungszettelzeile } from '@minikaenguru-ws/common-components';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelService {

	constructor(private http: HttpClient) { }

	public loadLoesungszettelWithID(kind: Kind): Observable<Loesungszettel> {

		if (!kind.punkte) {
			return throwError("invalid ID: kind hat keinen Lösungszettel");
		}

		const url = environment.apiUrl + '/loesungszettel/' + kind.punkte.loesungszettelId;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}


	public saveLoesungszettel(loesungszettel: Loesungszettel): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/loesungszettel';

		if (loesungszettel.uuid === 'neu') {
			return this.insertLoesungszettel(url, loesungszettel);
		}

		return this.updateLoesungszettel(url, loesungszettel);
	}

	public deleteLoesungszettel(loesungszettelID: string): Observable<ResponsePayload> {
		const url = environment.apiUrl + '/loesungszettel/' + loesungszettelID;

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

	private updateLoesungszettel(url: string, loesungszettel: Loesungszettel): Observable<ResponsePayload> {

		return this.http.put(url, loesungszettel).pipe(
			map(body => body as ResponsePayload)
		);

	}

}
