import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WettbewerbeListComponent } from './wettbewerbe-list/wettbewerbe-list.component';
import { StoreModule } from '@ngrx/store';
import { WettbewerbeEffects } from './+state/wettbewerbe.effects';

import * as fromWettbewerbe from './+state/wettbewerbe.reducer';
import { WettbewerbeListResolver } from './wettbewerbe-list/wettbewerbe-list.resolver';
import { EffectsModule } from '@ngrx/effects';
import { WettbewerbeRoutingModule } from './wettbewerbe-routing.module';


@NgModule({
	imports: [
		CommonModule,
		WettbewerbeRoutingModule,
		StoreModule.forFeature(fromWettbewerbe.wettbewerbeFeatureKey, fromWettbewerbe.reducer),
		EffectsModule.forFeature([WettbewerbeEffects])
	],
	declarations: [

		WettbewerbeListComponent], exports: [

		],
	providers: [
		WettbewerbeListResolver
	]
})
export class WettbewerbeModule { }
