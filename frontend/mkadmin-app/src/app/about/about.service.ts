import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { map, tap } from 'rxjs/operators';
import { ResponsePayload } from 'hewi-ng-lib';
import { LogService } from 'hewi-ng-lib';

@Injectable()
export class AboutService {


	constructor(private httpClient: HttpClient, private logger: LogService) { }

	loadAboutInfos(): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/version';

		return this.httpClient.get(url).pipe(
			tap(res => this.logger.debug(JSON.stringify(res))),
			map(res => res as ResponsePayload));
	}
}

