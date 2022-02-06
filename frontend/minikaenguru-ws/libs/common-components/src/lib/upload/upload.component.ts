import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { UploadService } from './upload.service';
import { UploadComponentModel } from '../common-components.model';
import { ResponsePayload, Message, ErrorMappingService } from '@minikaenguru-ws/common-messages';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mk-upload',
	templateUrl: './upload.component.html',
	styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {

	// adapted from https://www.bezkoder.com/angular-12-file-upload

	@Input()
	uploadModel!: UploadComponentModel;

	@Output()
	responsePayload: EventEmitter<ResponsePayload> = new EventEmitter<ResponsePayload>();

	@Output()
	dateiAusgewaehlt: EventEmitter<boolean> = new EventEmitter<boolean>();

	maxFileSizeInfo!: string;
	fileSize = '';
	

	uploading = false;
	uploadSuccessful = false;
	canSubmit = false;
	showMaxSizeExceeded = false;
	errmMaxFileSize = 'die gewählte Datei ist zu groß. Bitte wählen Sie eine andere Datei.'

	selectedFiles?: FileList;
  	currentFile?: File;
  	progress = 0;
  	message = '';

	constructor(private uploadService: UploadService, private errorMapper: ErrorMappingService, private logger: LogService) { }

	ngOnInit(): void {

		const maxFileSizeInKB = this.uploadModel.maxSizeBytes / 1024;
		const maxFileSizeInMB = maxFileSizeInKB / 1024;

		this.maxFileSizeInfo = 'Maximale erlaubte Größe: ' + maxFileSizeInKB + ' kB bzw. ' + maxFileSizeInMB + ' MB';
	}

	onFileAdded($event: any) {
		this.selectedFiles = $event.target.files;
		this.showMaxSizeExceeded = false;

		if (this.selectedFiles && this.selectedFiles.length === 1) {
			
			const size = this.selectedFiles[0].size;
			this.calculateFileSize(size);

			if (size <= this.uploadModel.maxSizeBytes) {				
				this.currentFile = this.selectedFiles[0];
				this.showMaxSizeExceeded = true;
				this.canSubmit = true;	
				this.dateiAusgewaehlt.emit(true);	
				this.uploading = false;	
			} else {
				this.showMaxSizeExceeded = true;
			}									
		}
	}



	submitUpload(): void {

		this.showMaxSizeExceeded = false;

		if (this.uploadSuccessful) {
			return;
		}

		if (!this.currentFile) {
			this.logger.debug('currentFile was undefined');
			return;
		}

		this.uploading = true;	

		this.uploadService.uploadSingleFile(this.currentFile, this.uploadModel.subUrl).subscribe(
			(rp: ResponsePayload) => {

				this.uploading = false;
				this.currentFile = undefined;
				this.canSubmit = false;
				this.responsePayload.emit(rp);

			},
			(error => {
				this.uploading = false;
				this.currentFile = undefined;
				this.canSubmit = true;

				const status = this.errorMapper.extractHttpStatusCode(error);			

				if (status === 0) {
					const msg: Message = {
						level: 'ERROR',
						message: 'Die Datei konnte nicht hochgeladen werden. Dateigröße: ' + this.fileSize + '. Bitte machen Sie einen Screenshot und senden diesen als Mail an info@egladil.de'
					};
					this.responsePayload.emit({ message: msg });
				} else {
					const msg: Message = this.errorMapper.extractMessageObject(error);
					this.responsePayload.emit({ message: msg });
				}				
				window.scroll(0,0);
			})
		);		
	}

	private calculateFileSize(size: number): void {

		let kb = size / 1024;

		console.log('kb: ' + kb);

		if (Math.round(size / 1024 ) < 2048) {
			this.fileSize = Math.round(size / 1024) + ' kB';					
		} else {
			this.fileSize = Math.round(size / 1024 / 1024) + ' MB';
		}
	}
}
