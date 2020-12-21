import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';
import { Loesungszettel, Loesungszettelzeile, createLoseungszettelzeilen } from './loesungszettel.model';
import { Kind, Klassenstufe } from '@minikaenguru-ws/common-components';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelService {

	constructor(private http: HttpClient) { }


	// vorerst
	public loadLoesungszettelWithID(kind: Kind): Observable<Loesungszettel> {


		const klassenstufe: Klassenstufe = kind.klassenstufe;
		const zeilen: Loesungszettelzeile[] = createLoseungszettelzeilen(klassenstufe.klassenstufe);

		const loesungszettel: Loesungszettel = {
			uuid: kind.loesungszettelId,
			kindID: kind.uuid,
			klassenstufe: kind.klassenstufe.klassenstufe,
			zeilen: zeilen
		};

		return of(loesungszettel);
	}

}
