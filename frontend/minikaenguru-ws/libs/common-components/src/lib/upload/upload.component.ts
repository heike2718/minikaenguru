import { Component, OnInit, ViewChild, Input, EventEmitter, Output } from '@angular/core';
import { UploadService } from './upload.service';
import { UploadComponentModel } from '../common-components.model';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';

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

	uploading = false;
	uploadSuccessful = false;
	canSubmit = false;

	constructor(private uploadService: UploadService) { }

	ngOnInit(): void {
	}

	onFileAdded() {
		this.dateiAusgewaehlt.emit(true);
		const files: { [key: string]: File } = this.file.nativeElement.files;
		for (const key in files) {
			if (!isNaN(parseInt(key, 0))) {
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
			rp => {

				this.uploading = false;
				this.selectedFile = undefined;
				this.canSubmit = false;
				this.responsePayload.emit(rp);

			}
		);
	}
}
