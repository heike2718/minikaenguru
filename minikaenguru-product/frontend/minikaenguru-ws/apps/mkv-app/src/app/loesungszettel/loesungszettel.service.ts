import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { Loesungszettel} from './loesungszettel.model';
import { Kind, Klassenstufe, LoesungszettelResponse, Loesungszettelzeile } from '@minikaenguru-ws/common-components';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public loadLoesungszettelWithID(kind: Kind): Observable<Loesungszettel> {

		if (!kind.punkte) {
			return throwError("invalid ID: kind hat keinen Lösungszettel");
		}

		const url = environment.apiUrl + '/loesungszettel/' + kind.punkte.loesungszettelId;

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
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

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.delete<ResponsePayload>(url));
	}

	// /////////////////////////////

	private insertLoesungszettel(url: string, loesungszettel: Loesungszettel): Observable<ResponsePayload> {

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post<ResponsePayload>(url, loesungszettel));

	}

	private updateLoesungszettel(url: string, loesungszettel: Loesungszettel): Observable<ResponsePayload> {

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.put<ResponsePayload>(url, loesungszettel));

	}

}
