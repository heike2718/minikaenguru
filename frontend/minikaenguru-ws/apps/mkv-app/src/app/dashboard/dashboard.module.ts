import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { DashboardComponent } from './dashboard.component';
import { DashboardResolver } from './dashboard.resolver';


@NgModule({
	imports: [
		CommonModule
	],
	declarations: [
		DashboardComponent
	],
	exports: [
		DashboardComponent
	],
	providers: [
		DashboardResolver
	]
})
export class DashboardModule {

}
