import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeilnahmenRoutingModule } from './teilnahmen-routing.module';
import * as fromTeilnahmen from './+state/teilnahmen.reducer';
import { StoreModule } from '@ngrx/store';
import { TeilnahmenListComponent } from './teilnahmen-list/teilnahmen-list.component';
import { TeilnahmenCardComponent } from './teilnahmen-card/teilnahmen-card.component';
import { TeilnahmenJahrResolver } from './teilnahmen-jahr.resolver';
import { MedianComponent } from './median/median.component';



@NgModule({
	declarations: [
		TeilnahmenListComponent,
		TeilnahmenCardComponent,
  MedianComponent
	],
	imports: [
		CommonModule,
		TeilnahmenRoutingModule,
		StoreModule.forFeature(fromTeilnahmen.teilnahmenFeatureKey, fromTeilnahmen.reducer)
	],
	providers: [
		TeilnahmenJahrResolver
	]
})
export class TeilnahmenModule { }
