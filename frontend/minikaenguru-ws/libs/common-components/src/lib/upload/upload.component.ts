import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { UploadService } from './upload.service';
import { forkJoin, Observable } from 'rxjs';
import { UploadComponentModel } from '../common-components.model';

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

	public files: Set<File> = new Set();

	// TODO: progress auswerten, loadingIndikator anzeigen oder sowas
	progress;

	uploading = false;
	uploadSuccessful = false;
	canSubmit = false;

	constructor(private uploadService: UploadService) { }

	ngOnInit(): void {
	}

	onFilesAdded() {
		const files: { [key: string]: File } = this.file.nativeElement.files;
		for (const key in files) {
			if (!isNaN(parseInt(key, 0))) {
				this.files.add(files[key]);
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

		this.progress = this.uploadService.upload(this.files, this.uploadModel.subUrl);

		console.log(this.progress);
		// tslint:disable-next-line: forin
		for (const key in this.progress) {
			this.progress[key].progress.subscribe(val => console.log(val));
		}

		// convert the progress map into an array
		const allProgressObservables = [];
		// tslint:disable-next-line: forin
		for (const key in this.progress) {
			allProgressObservables.push(this.progress[key].progress);
		}

		forkJoin(allProgressObservables).subscribe(end => {
			this.uploadSuccessful = true;
			this.uploading = false;
			this.canSubmit = false;
			this.files.clear();
		});
	}

}
