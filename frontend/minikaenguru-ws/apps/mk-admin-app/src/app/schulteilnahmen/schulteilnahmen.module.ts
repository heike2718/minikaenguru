import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromSchulteilnahmen from './+state/schulteilnahmen.reducer';
import { SchuleOverviewComponent } from './schule-overview/schule-overview.component';
import { SchulteilnahmenRoutingModule } from './schulteilnahmen-routing.module';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { UploadAuswertungComponent } from './upload-auswertung/upload-auswertung.component';


@NgModule({
	declarations: [
		SchuleOverviewComponent,
		UploadAuswertungComponent
	],
	imports: [
		FormsModule,
		CommonModule,
		CommonComponentsModule,
		SchulteilnahmenRoutingModule,
		StoreModule.forFeature(fromSchulteilnahmen.schulteilnahmenFeatureKey, fromSchulteilnahmen.reducer)
	]
})
export class SchulteilnahmenModule { }
