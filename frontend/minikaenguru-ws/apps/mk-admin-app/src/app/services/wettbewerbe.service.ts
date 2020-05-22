import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Wettbewerb } from '../wettbewerbe/wettbewerbe.model';

import { LogService } from '@minikaenguru-ws/common-logging';

@Injectable({
	providedIn: 'root'
})
export class WettbewerbeService {


	constructor(private http: HttpClient, private logger: LogService) { }


	public loadWettbewerbe(): Observable<Wettbewerb[]> {

		const url = environment.apiUrl + '/wb-admin/wettbewerbe';

		this.logger.debug(url);

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		)
	}
}
