import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MkComponentsConfig, MkComponentsConfigService } from '../configuration/mk-components-config';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable()
export class UploadService {

	constructor(private http: HttpClient, @Inject(MkComponentsConfigService) private config: MkComponentsConfig, private loadingIndicatorService: LoadingIndicatorService) { }

	public uploadSingleFile(file: File, url: string): Observable<ResponsePayload> {

		const formData: FormData = new FormData();
		formData.append('uploadedFile', file);
		formData.append('fileName', file.name);

		const completeUrl = this.config.baseUrl + url;

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post<ResponsePayload>(completeUrl, formData, { observe: 'body' }));
	}

}
