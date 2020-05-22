import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WettbewerbeListComponent } from './wettbewerbe-list/wettbewerbe-list.component';


@NgModule({
	imports: [
		CommonModule,
		// StoreModule
	],
	declarations: [

		WettbewerbeListComponent], exports: [

		],
	providers: [

	]
})
export class WettbewerbeModule { }
