import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Wettbewerb } from '../teilnahmen/teilnahmen.model';

@Injectable({
  providedIn: 'root'
})
export class TeilnahmenService {

  constructor(private http: HttpClient, private logger: LogService) { }


  getAktuellenWettbewerb(): Observable<Wettbewerb> {


	const url = environment.apiUrl + '/wettbewerb/aktueller';

		this.logger.debug('[SchulenService] findSchulen - url = ' + url);

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

  }

}
