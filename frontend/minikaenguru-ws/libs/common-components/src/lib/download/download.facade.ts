import { Injectable } from '@angular/core';
import { DownloadService } from './download.service';
import { take } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { DownloadState } from './+state/download.reducer';
import * as DownloadActions from './+state/download.actions';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { LogService } from '@minikaenguru-ws/common-logging';
import { downloadInProgress } from './+state/download.selectors';

@Injectable({
	providedIn: 'root'
})
export class DownloadFacade {

	public downloadInProgress$ = this.appStore.select(downloadInProgress);

	constructor(private downloadService: DownloadService,
		private appStore: Store<DownloadState>,
		private messageService: MessageService,
		private logger: LogService) { }

	public downloadFile(url: string, dateiname: string, mimetype: string): void {

		const filename = this.getFilename(dateiname, mimetype);

		this.appStore.dispatch(DownloadActions.startDownload());

		this.downloadService.downloadFile(url).pipe(
			take(1)
		).subscribe(
			blob => {
				this.saveAs(blob, filename);
				this.appStore.dispatch(DownloadActions.downloadFinished());
			},
			(error => {
				this.appStore.dispatch(DownloadActions.downloadFinished());
				this.handleError(error);
			})
		);
	}

	private saveAs(blob: Blob, filename: string): void {
		const a = document.createElement('a')
		const objectUrl = URL.createObjectURL(blob)
		a.href = objectUrl
		a.download = filename;
		a.click();
		URL.revokeObjectURL(objectUrl);
	}

	private getFilename(dateiname: string, mimetype: string): string {

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
