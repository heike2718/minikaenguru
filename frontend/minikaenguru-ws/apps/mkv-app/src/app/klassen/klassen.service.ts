import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Klasse, KlasseRequestData } from '@minikaenguru-ws/common-components';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class KlassenService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }


	public loadKlassen(schulkuerzel: string): Observable<Klasse[]> {

		const url = environment.apiUrl + '/klassen/' + schulkuerzel;

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}

	public insertKlasse(klasseRequestData: KlasseRequestData): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/klassen';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post<ResponsePayload>(url, klasseRequestData));
	}

	public updateKlasse(klasseRequestData: KlasseRequestData): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/klassen';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.put<ResponsePayload>(url, klasseRequestData));

	}

	public deleteKlasse(uuid: string): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/klassen/' + uuid;

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.delete<ResponsePayload>(url));

	}

	public deleteAllKlassen(schulkuerzel: string): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/lehrer/schulen/' + schulkuerzel + '/klassen';
		
		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.delete<ResponsePayload>(url));
	}
}
