import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DownloadComponent } from './download.component';
import { Routes, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HewiNgLibModule } from 'hewi-ng-lib';
import { UploadComponent } from './upload.component';
import { UploadService } from './upload.service';
import { DownloadService } from './download.service';


export const filesRoutes: Routes = [
	{
		path: 'download',
		component: DownloadComponent
	},
	{
		path: 'upload',
		component: UploadComponent
	},
];


@NgModule({
	imports: [
		CommonModule,
		FormsModule,
		HewiNgLibModule,
		RouterModule.forChild(filesRoutes),
	],
	declarations: [DownloadComponent, UploadComponent],
	exports: [
		DownloadComponent,
		UploadComponent
	],
	providers: [
		DownloadService,
		UploadService
	]

})
export class FilesModule { }
