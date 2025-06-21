import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EditAktuelleMeldungComponent } from './edit-aktuelle-meldung.component';
import { StoreModule } from '@ngrx/store';
import * as fromAktuelleMeldung from './+state/aktuelle-meldung.reducer';
import { FormsModule } from '@angular/forms';
import { AktuelleMeldungenRoutingModule } from './aktuelle-meldungen-routing.module';



@NgModule({
	imports: [
		CommonModule,
		FormsModule,
		AktuelleMeldungenRoutingModule,
		StoreModule.forFeature(fromAktuelleMeldung.aktuelleMeldungFeatureKey, fromAktuelleMeldung.reducer),
	],
	declarations: [
		EditAktuelleMeldungComponent
	],
	exports: [
		EditAktuelleMeldungComponent
	]
})
export class AktuelleMeldungModule { }
