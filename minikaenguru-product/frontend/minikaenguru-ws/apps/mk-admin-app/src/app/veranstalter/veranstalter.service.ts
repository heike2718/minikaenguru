import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Veranstalter, VeranstalterSuchanfrage, ZugangUnterlagen } from './veranstalter.model';
import { PrivatteilnahmeAdminOverview } from './teilnahmen.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class VeranstalterService {


	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }


	public findVeranstalter(suchanfrage: VeranstalterSuchanfrage): Observable<Veranstalter[]> {


		const url = environment.apiUrl + '/veranstalter/suche';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post(url, suchanfrage, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}

	public loadVeranstalterDetails(uuid: string): Observable<Veranstalter> {


		const url = environment.apiUrl + '/veranstalter/' + uuid;

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}


	public loadPrivatteilnahmeAdminOverview(teilnahmenummer: string): Observable<PrivatteilnahmeAdminOverview> {

		const url = environment.apiUrl + '/privatteilnahmen/' + teilnahmenummer;

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}

	public zugangsstatusUnterlagenAendern(veranstalter: Veranstalter, neuerStatus: ZugangUnterlagen): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/veranstalter/' + veranstalter.uuid + '/zugangsstatus';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post<ResponsePayload>(url, { 'zugangsstatus': neuerStatus }));
	}

	public newsletterDeaktivieren(veranstalter: Veranstalter): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/veranstalter/' + veranstalter.uuid + '/newsletter';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post<ResponsePayload>(url, null));

	}

}
