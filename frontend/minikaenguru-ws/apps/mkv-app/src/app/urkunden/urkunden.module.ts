import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import * as fromUrkunden from './+state/urkunden.reducer';
import { StoreModule } from '@ngrx/store';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { UrkundenauftragComponent } from './urkundenauftrag/urkundenauftrag.component';
import { UrkundenRoutingModule } from './urkunden-routing.module';



@NgModule({
	declarations: [
		UrkundenauftragComponent
	],
	imports: [
		CommonModule,
		BrowserModule,
		FormsModule,
		NgbModule,
		CommonComponentsModule, // für den downloadbutton
		UrkundenRoutingModule,
		StoreModule.forFeature(fromUrkunden.urkundenFeatureKey, fromUrkunden.urkundenReducer),

	]
})
export class UrkundenModule { }
