import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromSchulteilnahmen from './+state/schulteilnahmen.reducer';
import { SchuleOverviewComponent } from './schule-overview/schule-overview.component';
import { SchulteilnahmenRoutingModule } from './schulteilnahmen-routing.module';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';


@NgModule({
	declarations: [
		SchuleOverviewComponent
	],
  imports: [
	CommonModule,
	CommonComponentsModule,
	SchulteilnahmenRoutingModule,
	StoreModule.forFeature(fromSchulteilnahmen.schulteilnahmenFeatureKey, fromSchulteilnahmen.reducer)
  ]
})
export class SchulteilnahmenModule { }
