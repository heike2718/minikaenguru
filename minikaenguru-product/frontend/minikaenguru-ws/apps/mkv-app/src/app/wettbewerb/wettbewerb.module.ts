import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as fromWettbewerb from './+state/wettbewerb.reducer';
import { StoreModule } from '@ngrx/store';
import { WettbewerbInfoComponent } from './wettbewerb-info.component';

@NgModule({
	imports: [
		CommonModule,
		StoreModule.forFeature(fromWettbewerb.wettbewerbFeatureKey, fromWettbewerb.reducer),
	],
	declarations: [
		WettbewerbInfoComponent
	],
	exports: [
		WettbewerbInfoComponent
	]
})
export class WettbewerbModule {

}

