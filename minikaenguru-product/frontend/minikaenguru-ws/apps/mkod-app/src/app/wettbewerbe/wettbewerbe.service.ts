import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Wettbewerb } from './wettbewerb.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class WettbewerbeService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public loadWettbewerbe(): Observable<Wettbewerb[]> {

		const url = environment.apiUrl + '/wettbewerbe';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}
}
