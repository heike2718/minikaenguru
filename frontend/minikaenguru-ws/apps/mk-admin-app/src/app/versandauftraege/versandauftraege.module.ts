import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';

import * as fromVersandauftraege from './+state/versandauftraege.reducer';
import { VersandauftraegeRoutingModule } from './versandauftraege-routing.module';
import { VersandauftraegeListComponent } from './versandauftraege-list/versandauftraege-list.component';
import { VersandauftragCardComponent } from './versandauftrag-card/versandauftrag-card.component';



@NgModule({
	declarations: [
		VersandauftraegeListComponent,
		VersandauftragCardComponent
	],
	imports: [
		CommonModule,
		VersandauftraegeRoutingModule,
		StoreModule.forFeature(fromVersandauftraege.versandauftraegeFeatureKey, fromVersandauftraege.reducer)
	]
})
export class VersandauftraegeModule { }
