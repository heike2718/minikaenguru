import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromWettbewerbe from './+state/wettbewerbe.reducer';
import { WettbewerbeCardComponent } from './wettbewerbe-card/wettbewerbe-card.component';
import { WettbewerbeListComponent } from './wettbewerbe-list/wettbewerbe-list.component';
import { WettbewerbeRoutingModule } from './wettbewerbe-routing.module';



@NgModule({
	declarations: [	
		WettbewerbeListComponent,
    	WettbewerbeCardComponent
  ],
	imports: [
		CommonModule,
		WettbewerbeRoutingModule,
		StoreModule.forFeature(fromWettbewerbe.wettbewerbeFeatureKey, fromWettbewerbe.reducer)
	],
	providers: [

	]
})
export class WettbewerbeModule { }
