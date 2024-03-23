import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { VertragAdvEditorModel } from './vertrag-adv.model';
import { Observable } from 'rxjs';
import { Message, ResponsePayload } from '@minikaenguru-ws/common-messages';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class VertrtagAdvService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public submitVertrag(vertrag: VertragAdvEditorModel): Observable<Message> {

		const url = environment.apiUrl + '/adv';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post(url, vertrag, {observe: 'body'}).pipe(
			map(response => response as ResponsePayload),
			map (rp => rp.message)
		));
	}
}
