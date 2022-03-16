import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Mustertext } from '../shared/shared-entities.model';


@Injectable({
	providedIn: 'root'
})
export class MustertexteService {

    constructor(private http: HttpClient) { }

    public loadMustertexte(): Observable<Mustertext[]> {

        const url = environment.apiUrl + '/mustertexte';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
    }

	public loadMustertext(uuid: string): Observable<Mustertext> {

        const url = environment.apiUrl + '/mustertexte/' + uuid;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
    }

	public insertMustertext(mustertext: Mustertext): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/mustertexte';

		return this.http.post(url, mustertext).pipe(
			map(body => body as ResponsePayload)
		);
	}

	public updateMustertext(mustertext: Mustertext): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/mustertexte/' + mustertext.uuid;

		return this.http.put(url, mustertext).pipe(
			map(body => body as ResponsePayload)
		);
	}

	public deleteMustertext(mustertext: Mustertext): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/mustertexte/' + mustertext.uuid;

		return this.http.delete(url).pipe(
			map(body => body as ResponsePayload)
		);
	}
};
