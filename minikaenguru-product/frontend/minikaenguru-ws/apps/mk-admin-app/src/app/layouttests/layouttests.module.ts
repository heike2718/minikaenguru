import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IrgendwasListComponent } from './irgendwas-list/irgendwas-list.component';
import { IrgendwasCardComponent } from './irgendwas-card/irgendwas-card.component';



@NgModule({
	declarations: [
		IrgendwasListComponent,
		IrgendwasCardComponent
	],
	imports: [
		CommonModule
	],
	exports: [
		IrgendwasListComponent
	]
})
export class LayouttestsModule { }
