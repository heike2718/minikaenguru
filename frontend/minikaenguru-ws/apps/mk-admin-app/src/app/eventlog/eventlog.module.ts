import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventlogComponent } from './eventlog.component';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StoreModule } from '@ngrx/store';
import * as fromEventlogReducer from './+state/eventlog.reducer';
import { EventlogRoutingModule } from './eventlog-routing.module';



@NgModule({
	declarations: [
		EventlogComponent
	],
	imports: [
		CommonModule,
		BrowserModule,
		FormsModule,
		NgbModule,
		CommonComponentsModule,
		StoreModule.forFeature(fromEventlogReducer.eventlogFeatureKey, fromEventlogReducer.eventlogReducer),
		EventlogRoutingModule
	]
})
export class EventlogModule { }
