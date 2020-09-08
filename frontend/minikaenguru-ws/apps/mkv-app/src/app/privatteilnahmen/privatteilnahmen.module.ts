import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromPrivat from './+state/privatteilnahmen.reducer';
import { PrivatteilnahmenRoutingModule } from './privatteilnahmen-routing.module';
import { PrivatteilnahmenListComponent } from './privatteilnahmen-list/privatteilnahmen-list.component';
import { PrivatteilnahmeCardComponent } from './privatteilnahme-card/privatteilnahme-card.component';


@NgModule({
	imports: [
		CommonModule,
		StoreModule.forFeature(fromPrivat.privatteilnahmenFeatureKey, fromPrivat.reducer),
		PrivatteilnahmenRoutingModule
	],
	declarations: [
		PrivatteilnahmenListComponent,
		PrivatteilnahmeCardComponent
	],
	exports: [
		PrivatteilnahmenListComponent
	],
	providers: [
	]
})
export class PrivatteilnahmenModule {};
