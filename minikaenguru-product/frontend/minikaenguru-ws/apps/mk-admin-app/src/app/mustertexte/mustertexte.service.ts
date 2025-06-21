import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Message, ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Mail, Mustertext } from '../shared/shared-entities.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';


@Injectable({
	providedIn: 'root'
})
export class MustertexteService {

    constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

    public loadMustertexte(): Observable<Mustertext[]> {

        const url = environment.apiUrl + '/mustertexte';

		const obs$ = this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
    }

	public loadMustertext(uuid: string): Observable<Mustertext> {

        const url = environment.apiUrl + '/mustertexte/' + uuid;

		const obs$ = this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
    }

	public insertMustertext(mustertext: Mustertext): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/mustertexte';

		const obs$ = this.http.post(url, mustertext).pipe(
			map(body => body as ResponsePayload)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public updateMustertext(mustertext: Mustertext): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/mustertexte/' + mustertext.uuid;

		const obs$ = this.http.put(url, mustertext).pipe(
			map(body => body as ResponsePayload)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public deleteMustertext(mustertext: Mustertext): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/mustertexte/' + mustertext.uuid;

		const obs$ = this.http.delete(url).pipe(
			map(body => body as ResponsePayload)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public sendMail(mail: Mail): Observable<Message> {

		const url = environment.apiUrl + '/mails';

		const obs$ = this.http.post(url, mail).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.message)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}
};
