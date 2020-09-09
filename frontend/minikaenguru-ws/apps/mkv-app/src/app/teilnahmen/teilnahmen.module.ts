import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromTeilnahmen from './+state/teilnahmen.reducer';
import { TeilnahmenRoutingModule } from './teilnahmen-routing.module';
import { TeilnahmenListComponent } from './teilnahmen-list/teilnahmen-list.component';
import { TeilnahmeCardComponent } from './teilnahme-card/teilnahme-card.component';
import { TeilnahmenListResolver } from './teilnahmen-list/teilnahmen-list.resolver';


@NgModule({
	imports: [
		CommonModule,
		StoreModule.forFeature(fromTeilnahmen.teilnahmenFeatureKey, fromTeilnahmen.reducer),
		TeilnahmenRoutingModule
	],
	declarations: [
		TeilnahmenListComponent,
		TeilnahmeCardComponent
	],
	exports: [
		TeilnahmenListComponent
	],
	providers: [
		TeilnahmenListResolver
	]
})
export class TeilnahmenModule {};
