import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { ResponsePayload, Message } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { Privatveranstalter, Lehrer } from '../wettbewerb/wettbewerb.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';


@Injectable({
	providedIn: 'root'
})
export class VeranstalterService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	getLehrer(): Observable<Lehrer> {

		const url = environment.apiUrl + '/lehrer';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}

	public loadPrivatveranstalter(): Observable<Privatveranstalter> {

		const url = environment.apiUrl + '/veranstalter/privat';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));

	}

	public toggleAboNewsletter(): Observable<Message> {

		const url = environment.apiUrl + '/veranstalter/newsletter';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.put(url, {}).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.message)
		));
	}

	public addSchule(kuerzel: string): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/lehrer/schulen/' + kuerzel;

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post<ResponsePayload>(url, {}))
	}

	public removeSchule(kuerzel: string): Observable<Message> {
		const url = environment.apiUrl + '/lehrer/schulen/' + kuerzel;

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.delete(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.message)
		));
	}
}
