import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpEventType, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable, Subject } from 'rxjs';
import { HttpErrorHandler } from '../error/http-error-handler.service';

const URL = environment.apiUrl + '/files/file';


@Injectable()
export class UploadService {

	constructor(private http: HttpClient, private httpErrorService: HttpErrorHandler) { }

	public upload(
		files: Set<File>
	): { [key: string]: { progress: Observable<number> } } {

		// this will be the our resulting map
		const status: { [key: string]: { progress: Observable<number> } } = {};

		files.forEach(file => {
			// create a new multipart-form for every file
			const formData: FormData = new FormData();
			formData.append('uploadedFile', file, file.name);

			const req = new HttpRequest('POST', URL, formData, {
				reportProgress: true
			});

			// create a new progress-subject for every file
			const progress = new Subject<number>();

			// send the http-request and subscribe for progress-updates
			this.http.request(req).subscribe(event => {

				if (event.type === HttpEventType.UploadProgress) {

					// calculate the progress percentage
					const percentDone = Math.round(100 * event.loaded / event.total);

					// pass the percentage into the progress-stream
					progress.next(percentDone);
				} else if (event.type === HttpEventType.Response) {
					// Close the progress-stream if we get an answer form the API
					// The upload is complete
					progress.complete();
				}
			});

			// Save every progress-observable in a map of all observables
			status[file.name] = {
				progress: progress.asObservable()
			};
		});

		// return the map of progress.observables
		return status;
	}
}
