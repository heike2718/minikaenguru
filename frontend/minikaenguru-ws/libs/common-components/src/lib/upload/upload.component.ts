import { Component, OnInit, ViewChild, Input, EventEmitter, Output } from '@angular/core';
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

	uploading = false;
	uploadSuccessful = false;
	canSubmit = false;

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

		if (this.selectedFiles && this.selectedFiles.length === 1) {
			this.currentFile = this.selectedFiles[0];
			this.canSubmit = true;
			this.uploading = false;
			this.dateiAusgewaehlt.emit(true);			
		}
	}

	submitUpload(): void {

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
				this.canSubmit = true;
				const msg: Message = this.errorMapper.extractMessageObject(error);
				this.responsePayload.emit({ message: msg });
			})
		);		
	}
}
