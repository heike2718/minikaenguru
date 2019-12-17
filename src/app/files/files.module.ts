import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DownloadComponent } from './download.component';
import { Routes, RouterModule } from '@angular/router';
import { DownloadService } from './download.service';


export const filesRoutes: Routes = [
	{
		path: '',
		component: DownloadComponent
	},
];


@NgModule({
	imports: [
		CommonModule,
		RouterModule.forChild(filesRoutes),
	],
	declarations: [DownloadComponent],
	exports: [
		DownloadComponent
	],
	providers: [
		DownloadService
	]

})
export class FilesModule { }
