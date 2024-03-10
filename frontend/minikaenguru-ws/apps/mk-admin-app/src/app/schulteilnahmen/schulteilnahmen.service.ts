import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { SchuleAdminOverview } from './schulteilnahmen.model';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class SchulteilnahmenService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }



	public loadSchuleAdminOverview(schulkuerzel: string): Observable<SchuleAdminOverview> {

		const url = environment.apiUrl + '/schulen/' + schulkuerzel;

		const obs$ =  this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

}
