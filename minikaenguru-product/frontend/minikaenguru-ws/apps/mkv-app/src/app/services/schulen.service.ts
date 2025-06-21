import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Schule } from '../lehrer/schulen/schulen.model';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';


@Injectable({
	providedIn: 'root'
})
export class SchulenService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }


	findSchulen(): Observable<Schule[]> {

		const url = environment.apiUrl + '/lehrer/schulen';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}

	public loadDetails(schulkuerzel: string): Observable<Schule> {

		const url = environment.apiUrl + '/lehrer/schulen/' + schulkuerzel + '/details';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}
}
