import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WettbewerbInfoComponent } from './wettbewerb/wettbewerb-info.component';
import * as fromTeilnahmen from './+state/teilnahmen.reducer';
import { StoreModule } from '@ngrx/store';



@NgModule({
	declarations: [
		WettbewerbInfoComponent,

	],
	imports: [
		CommonModule,
		StoreModule.forFeature(fromTeilnahmen.teilnahmenFeatureKey, fromTeilnahmen.reducer),
	],
	exports: [
		WettbewerbInfoComponent
	]
})
export class TeilnahmenModule { }
