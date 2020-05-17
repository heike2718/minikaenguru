import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as fromVeranstalter from './+state/veranstalter.reducer';
import { DashboardComponent } from './dashboard.component';
import { VeranstalterEffects } from './+state/veranstalter.effects';
import { EffectsModule } from '@ngrx/effects';
import { DashboardResolver } from './dashboard.resolver';


@NgModule({
	imports: [
		CommonModule,
		StoreModule.forFeature(fromVeranstalter.veranstalterFeatureKey, fromVeranstalter.reducer),
		EffectsModule.forFeature([VeranstalterEffects])
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
