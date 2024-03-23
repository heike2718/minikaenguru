import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Anmeldungen } from '../shared/beteiligungen.model';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class AnmeldungenService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public loadAnmeldungen(): Observable<Anmeldungen> {

		const url = environment.apiUrl + '/open-data/statistik/anmeldungen';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		));
	}
}
