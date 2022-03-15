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


};
