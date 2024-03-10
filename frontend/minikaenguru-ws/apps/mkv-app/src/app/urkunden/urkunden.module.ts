import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { UrkundenauftragComponent } from './urkundenauftrag/urkundenauftrag.component';
import { UrkundenRoutingModule } from './urkunden-routing.module';
import { AuswertungsaufragComponent } from './auswertungsaufrag/auswertungsaufrag.component';



@NgModule({
	declarations: [
		UrkundenauftragComponent,
		AuswertungsaufragComponent
	],
	imports: [
		CommonModule,
		BrowserModule,
		FormsModule,
		NgbModule,
		CommonComponentsModule, // für den downloadbutton
		UrkundenRoutingModule,
	]
})
export class UrkundenModule { }
