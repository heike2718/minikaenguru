import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';
import { Loesungszettel, Loesungszettelzeile, createLoseungszettelzeilen, LoesungszettelResponseModel } from './loesungszettel.model';
import { Kind, Klassenstufe, LoesungszettelPunkte } from '@minikaenguru-ws/common-components';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelService {

	constructor(private http: HttpClient) { }


	// vorerst
	public loadLoesungszettelWithID(kind: Kind): Observable<LoesungszettelResponseModel> {


		const klassenstufe: Klassenstufe = kind.klassenstufe;
		const zeilen: Loesungszettelzeile[] = createLoseungszettelzeilen(klassenstufe.klassenstufe);

		const loesunszettelpunkte: LoesungszettelPunkte = {
			loesungszettelId: 'b1adfe89-b266-4859-b46a-9739f00a07cd',
			punkte: '23,75',
			laengeKaengurusprung: 2
		};

		const loesungszettel: Loesungszettel = {
			uuid: loesunszettelpunkte.loesungszettelId,
			kindID: kind.uuid,
			klassenstufe: kind.klassenstufe.klassenstufe,
			zeilen: zeilen
		};

		return of({ loesungszettel: loesungszettel, punkte: loesunszettelpunkte });
	}


	public saveLoesungszettel(loesungszettel: Loesungszettel): Observable<ResponsePayload> {

		const loesunszettelpunkte: LoesungszettelPunkte = {
			loesungszettelId: 'b1adfe89-b266-4859-b46a-9739f00a07cd',
			punkte: '48,5,75',
			laengeKaengurusprung: 4
		};

		const loesungszettelResponse: LoesungszettelResponseModel = {
			loesungszettel: loesungszettel,
			punkte: loesunszettelpunkte
		};


		const responsePayload: ResponsePayload = {
			message: { level: 'INFO', message: 'Der Lösungszettel wurde erfolgreich gespeichert' },
			data: loesungszettelResponse
		};

		return of(responsePayload);
	}

}
