import { Injectable } from '@angular/core';
import { DownloadService } from './download.service';
import { take } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { DownloadState } from './+state/download.reducer';
import * as DownloadActions from './+state/download.actions';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { LogService } from '@minikaenguru-ws/common-logging';
import { downloadId, downloadInProgress } from './+state/download.selectors';

@Injectable({
	providedIn: 'root'
})
export class DownloadFacade {

	public downloadInProgress$ = this.appStore.select(downloadInProgress);
	public downloadId$ = this.appStore.select(downloadId);


	constructor(private downloadService: DownloadService,
		private appStore: Store<DownloadState>,
		private messageService: MessageService,
		private logger: LogService) { }

	public downloadFile(id: string, url: string, dateiname: string, mimetype: string): void {

		const defaultFilename = this.getDefaultFilename(dateiname, mimetype);

		this.appStore.dispatch(DownloadActions.startDownload({id: id}));

		this.downloadService.downloadFile(url).pipe(
			take(1)
		).subscribe(
			blob => {
				this.saveAs(blob, defaultFilename);
				this.appStore.dispatch(DownloadActions.downloadFinished());
			},
			(error => {
				this.appStore.dispatch(DownloadActions.downloadFinished());
				this.handleError(error);
			})
		);
	}

	private saveAs(httpResponse: HttpResponse<any>, defaultFilename: string): void {

		const contentDispositionHeader = httpResponse.headers.get('Content-Disposition');
		const filename = this.getFilenameFromContentDispositionHeader(contentDispositionHeader, defaultFilename);

		const a = document.createElement('a')
		const objectUrl = URL.createObjectURL(httpResponse.body)
		a.href = objectUrl
		a.download = filename;
		a.click();
		URL.revokeObjectURL(objectUrl);
	}

	private getFilenameFromContentDispositionHeader(contentDispositionHeader: string, defaultFilename: string) {

		if (!contentDispositionHeader) {
			return defaultFilename;
		}

		const indexOfFilenameSubstring = contentDispositionHeader.indexOf('filename=');

		if (indexOfFilenameSubstring < 0) {
			return defaultFilename;
		}

		const startIndex = indexOfFilenameSubstring + 'filename='.length;

		return contentDispositionHeader.substr(startIndex, contentDispositionHeader.length -1);

	}

	private getDefaultFilename(dateiname: string, mimetype: string): string {

		if (dateiname && mimetype) {
			return dateiname + '.' + mimetype;
		}
		if (dateiname && !mimetype) {
			return dateiname;
		}
		if (!dateiname && mimetype) {
			return 'download.' + mimetype;
		}

		return 'download';
	}



	private handleError(error: HttpErrorResponse | Error): void {

		if (error instanceof HttpErrorResponse) {
			const httpError = error as HttpErrorResponse;
			console.log('HttpErrorResponse: ' + httpError.status);
			this.handleHttpError(httpError);
		} else {
			console.log('ErrorEvent: ' + error);
			this.logger.error('mkv-app: Unerwarteter Fehler: ' + error.message);
			this.showServerResponseMessage('ERROR', 'Unerwarteter GUI-Error: ' + error.message);
		}
	}

	private handleHttpError(httpError: HttpErrorResponse) {
		if (httpError.status === 0) {
			this.messageService.error('Der Server ist nicht erreichbar.');
		} else {
			switch (httpError.status) {
				case 401:
					this.messageService.error('Sie haben keine Berechtigung. Bitte loggen Sie sich ein.');
					break;
				case 403:
					this.messageService.error('Sie haben keine Berechtigung, diese Resource aufzurufen.');
					break;
				case 908:
					this.messageService.error('Ihre Session ist abgelaufen. Bitte loggen Sie sich erneut ein.');
					break;
				default: {
					this.messageService.error('Es ist ein unerwarteter Fehler aufgetreten. Bitte schreiben Sie eine Mail an minikaenguru@egladil.de');
				}
			}
		}
	}

	private showServerResponseMessage(level: string, message: string) {

		switch (level) {
			case 'WARN':
				this.messageService.error(message);
				break;
			case 'ERROR':
				this.messageService.error(message);
				break;
			default:
				this.messageService.error('Unbekanntes message.level ' + level + ' vom Server bekommen.');
		}
	}

}
