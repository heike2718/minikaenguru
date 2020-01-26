import { Component, OnInit } from '@angular/core';
import { DownloadService } from './download.service';
import * as fileSaver from 'file-saver'; // npm i --save file-saver

@Component({
	selector: 'mkadm-download',
	templateUrl: './download.component.html',
	styleUrls: ['./download.component.scss']
})
export class DownloadComponent implements OnInit {

	fileSystemName: string;

	constructor(private downloadService: DownloadService) { }

	ngOnInit() {
	}



	downloadCsv() {

		console.log('vor download');

		this.downloadService.downloadCsv().subscribe(response => {
			const contentDisposition = response.headers.get('Content-Disposition');
			const filename = 'test.csv';

			const blob: any = new Blob([response.body], { type: 'text/csv; charset=utf-8' });
			// const url = window.URL.createObjectURL(blob);
			// window.open(url);
			fileSaver.saveAs(blob, 'test.csv');
		});
	}

	downloadPdf() {

		this.downloadService.downloadPdf().subscribe(response => {
			const contentDisposition = response.headers.get('Content-Disposition');
			const blob: any = new Blob([response.body], { type: 'application/pdf' });
			// const url = window.URL.createObjectURL(blob);
			// window.open(url);
			fileSaver.saveAs(blob, 'test.pdf');
		});
	}


	downloadExcel() {

		this.downloadService.downloadExcel().subscribe(response => {

			const filename = response.headers.get('filename');

			const contentDisposition = response.headers.get('Content-Disposition');
			const blob: any = new Blob([response.body], { type: 'application/vnd.ms-excel' });
			// const url = window.URL.createObjectURL(blob);
			// window.open(url);
			fileSaver.saveAs(blob, 'test.xls');
		});
	}

}
