import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogComponent } from './dialog/dialog.component';
import { FormErrorComponent } from './form-error/form-error.component';

@NgModule({
	imports: [
		CommonModule
	],
	declarations: [
		DialogComponent,
		FormErrorComponent
	],
	exports: [
		DialogComponent,
		FormErrorComponent
	]
})
export class CommonComponentsModule { }
