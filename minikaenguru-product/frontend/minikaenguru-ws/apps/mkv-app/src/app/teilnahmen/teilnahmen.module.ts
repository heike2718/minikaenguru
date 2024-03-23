import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromTeilnahmen from './+state/teilnahmen.reducer';
import { TeilnahmenRoutingModule } from './teilnahmen-routing.module';
import { TeilnahmenListComponent } from './teilnahmen-list/teilnahmen-list.component';
import { TeilnahmenListResolver } from './teilnahmen-list/teilnahmen-list.resolver';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';


@NgModule({
	imports: [
		CommonModule,
		CommonComponentsModule,
		StoreModule.forFeature(fromTeilnahmen.teilnahmenFeatureKey, fromTeilnahmen.reducer),
		TeilnahmenRoutingModule
	],
	declarations: [
		TeilnahmenListComponent
	],
	exports: [
		TeilnahmenListComponent
	],
	providers: [
		TeilnahmenListResolver
	]
})
export class TeilnahmenModule {};
