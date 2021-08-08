import { Component, OnInit, ViewChild, Input, EventEmitter, Output } from '@angular/core';
import { UploadService } from './upload.service';
import { UploadComponentModel } from '../common-components.model';
import { ResponsePayload, Message, MessageLevel, ErrorMappingService } from '@minikaenguru-ws/common-messages';

@Component({
	selector: 'mk-upload',
	templateUrl: './upload.component.html',
	styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {

	// https://www.bezkoder.com/angular-11-node-js-file-upload/

	@ViewChild('file', { static: false }) file;

	@Input()
	uploadModel: UploadComponentModel;

	@Output()
	responsePayload: EventEmitter<ResponsePayload> = new EventEmitter<ResponsePayload>();

	@Output()
	dateiAusgewaehlt: EventEmitter<boolean> = new EventEmitter<boolean>();

	// public files: Set<File> = new Set();

	public selectedFile: File;

	public maxFileSizeInfo: string;

	uploading = false;
	uploadSuccessful = false;
	canSubmit = false;

	constructor(private uploadService: UploadService, private errorMapper: ErrorMappingService) { }

	ngOnInit(): void {

		const maxFileSizeInKB = this.uploadModel.maxSizeBytes / 1024;
		const maxFileSizeInMB = maxFileSizeInKB / 1024;

		this.maxFileSizeInfo = 'Maximale erlaubte Größe: ' + maxFileSizeInKB + ' kB bzw. ' + maxFileSizeInMB + ' MB';
	}

	onFileAdded() {
		this.dateiAusgewaehlt.emit(true);
		const files: { [key: string]: File } = this.file.nativeElement.files;
		for (const key in files) {
			if (!isNaN(parseInt(key, 0))) {

				if (files[key].size > this.uploadModel.maxSizeBytes) {
					this.canSubmit = false;
					const msg: Message = { level: 'ERROR', message: this.uploadModel.errorMessageSize };
					this.responsePayload.emit({ message: msg });
				}

				this.selectedFile = files[key];
			}
		}
	}

	addFiles() {
		this.file.nativeElement.click();
		this.canSubmit = true;
	}

	submitUpload(): void {

		if (this.uploadSuccessful) {
			return;
		}

		this.uploading = true;

		this.uploadService.uploadSingleFile(this.selectedFile, this.uploadModel.subUrl).subscribe(
			(rp: ResponsePayload) => {

				this.uploading = false;
				this.selectedFile = undefined;
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
