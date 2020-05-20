import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as fromVeranstalter from './+state/veranstalter.reducer';
import { DashboardComponent } from './dashboard.component';


@NgModule({
	imports: [
		CommonModule,
		StoreModule.forFeature(fromVeranstalter.veranstalterFeatureKey, fromVeranstalter.reducer)
	],
	declarations: [
		DashboardComponent
	],
	exports: [
		DashboardComponent
	],
	providers: [
	]
})
export class DashboardModule {

}
