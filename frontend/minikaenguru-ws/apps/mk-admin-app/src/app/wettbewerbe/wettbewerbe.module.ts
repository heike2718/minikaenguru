import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WettbewerbeListComponent } from './wettbewerbe-list/wettbewerbe-list.component';
import { StoreModule } from '@ngrx/store';
import { WettbewerbeEffects } from './+state/wettbewerbe.effects';

import * as fromWettbewerbe from './+state/wettbewerbe.reducer';
import { WettbewerbeListResolver } from './wettbewerbe-list/wettbewerbe-list.resolver';
import { EffectsModule } from '@ngrx/effects';
import { WettbewerbeRoutingModule } from './wettbewerbe-routing.module';
import { WettbewerbCardComponent } from './wettbewerb-card/wettbewerb-card.component';
import { WettbewerbDashboardComponent } from './wettbewerb-dashboard/wettbewerb-dashboard.component';
import { WettbewerbDashboardResolver } from './wettbewerb-dashboard/wettbewerb-dashboard.resolver';


@NgModule({
	imports: [
		CommonModule,
		WettbewerbeRoutingModule,
		StoreModule.forFeature(fromWettbewerbe.wettbewerbeFeatureKey, fromWettbewerbe.reducer),
		EffectsModule.forFeature([WettbewerbeEffects])
	],
	declarations: [

		WettbewerbeListComponent,
		WettbewerbCardComponent,
		WettbewerbDashboardComponent],
	exports: [
		],
	providers: [
		WettbewerbeListResolver,
		WettbewerbDashboardResolver
	]
})
export class WettbewerbeModule { }
