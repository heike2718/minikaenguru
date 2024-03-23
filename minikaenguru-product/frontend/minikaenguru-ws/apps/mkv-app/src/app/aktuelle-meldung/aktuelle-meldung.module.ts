import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as fromAktuelleMeldung from './+state/aktuelle-meldung.reducer';
import { StoreModule } from '@ngrx/store';
import { AktuelleMeldungComponent } from './aktuelle-meldung.component';


@NgModule({
	imports: [
		CommonModule,
		StoreModule.forFeature(fromAktuelleMeldung.aktuelleMeldungFeatureKey, fromAktuelleMeldung.reducer),
	],
	declarations: [
		AktuelleMeldungComponent
	],
	exports: [
		AktuelleMeldungComponent
	]
})
export class AktuelleMeldungModule {}
