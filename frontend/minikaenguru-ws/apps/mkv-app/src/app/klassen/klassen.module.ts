import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KlassenListComponent } from './klassen-list/klassen-list.component';
import { KlasseDetailsComponent } from './klasse-details/klasse-details.component';
import { KlassenRoutingModule } from './klassen-routing.module';
import { StoreModule } from '@ngrx/store';
import * as fromKlassen from './+state/klassen.reducer';
import { AppRoutingModule } from '../app-routing.module';



@NgModule({
	declarations: [
		KlassenListComponent,
		KlasseDetailsComponent
	],
	imports: [
		CommonModule,
		KlassenRoutingModule,
		AppRoutingModule,
		StoreModule.forFeature(fromKlassen.klassenFeatureKey, fromKlassen.reducer)
	]
})
export class KlassenModule { }
