import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { AktuelleMeldung } from './aktuelle-meldung.model';
import { Observable } from 'rxjs';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
  providedIn: 'root'
})
export class AktuelleMeldungService {

  constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

  public loadAktuelleMeldung(): Observable<AktuelleMeldung> {


	const url = environment.apiUrl + '/meldungen/aktuelle-meldung';

	return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
		map(body => body as ResponsePayload),
		map(payload => payload.data)
	));
  }
}
