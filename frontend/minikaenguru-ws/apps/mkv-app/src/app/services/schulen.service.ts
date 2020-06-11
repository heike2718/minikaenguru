import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Observable } from 'rxjs';
import { Schule, SchuleDashboardModel } from '../schulen/schulen.model';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';


@Injectable({
	providedIn: 'root'
})
export class SchulenService {

	constructor(private http: HttpClient, private logger: LogService) { }

	findSchulen(): Observable<Schule[]> {

		const url = environment.apiUrl + '/wettbewerb/lehrer/schulen';

		this.logger.debug('[SchulenService] findSchulen - url = ' + url);

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public loadDetails(schule: Schule): Observable<SchuleDashboardModel> {

		const url = environment.apiUrl + '/wettbewerb/lehrer/schulen/' + schule.kuerzel + '/details';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}

}
