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
import { WettbewerbDetailsResolver } from './wettbewerb-details.resolver';
import { WettbewerbEditorComponent } from './wettbewerb-editor/wettbewerb-editor.component';
import { ReactiveFormsModule } from '@angular/forms';


@NgModule({
	declarations: [
		WettbewerbeListComponent,
		WettbewerbCardComponent,
		WettbewerbDashboardComponent,
		WettbewerbEditorComponent
	],
	imports: [
		CommonModule,
		ReactiveFormsModule,
		WettbewerbeRoutingModule,
		StoreModule.forFeature(fromWettbewerbe.wettbewerbeFeatureKey, fromWettbewerbe.reducer),
		EffectsModule.forFeature([WettbewerbeEffects])
	],
	providers: [
		WettbewerbeListResolver,
		WettbewerbDetailsResolver
	]
})
export class WettbewerbeModule { }
