import { Injectable, Inject } from '@angular/core';
import { HttpClient, HttpRequest, HttpEventType, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { MkComponentsConfig, MkComponentsConfigService } from '../configuration/mk-components-config';

@Injectable()
export class UploadService {

	constructor(private http: HttpClient, @Inject(MkComponentsConfigService) private config: MkComponentsConfig) { }

	public upload(
		files: Set<File>, url: string
	): { [key: string]: { progress: Observable<number> } } {

		const status: { [key: string]: { progress: Observable<number> } } = {};
		const completeUrl = this.config.baseUrl + url;

		files.forEach(file => {

			const formData: FormData = new FormData();
			formData.append('uploadedFile', file);
			formData.append('fileName', file.name);

			const req = new HttpRequest('POST', completeUrl, formData, {
				reportProgress: true
			});

			const progress = new Subject<number>();

			this.http.request(req).subscribe(event => {

				if (event.type === HttpEventType.UploadProgress) {
					const percentDone = Math.round(100 * event.loaded / event.total);

					progress.next(percentDone);

				} else if (event.type === HttpEventType.Response) {

					progress.complete();
				}

			});

			status[file.name] = {
				progress: progress.asObservable()
			};

		});

		return status;

	}

}
