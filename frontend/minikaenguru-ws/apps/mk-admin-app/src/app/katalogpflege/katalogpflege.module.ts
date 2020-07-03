import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { ReactiveFormsModule } from '@angular/forms';
import { KatalogpflegeRoutingModule } from './katalogpflege-routing.module';
import { KatalogpflegeComponent } from './katalogpflege.component';
import { EditOrtComponent } from './edit-ort/edit-ort.component';
import { EditLandComponent } from './edit-land/edit-land.component';
import { EditSchuleComponent } from './edit-schule/edit-schule.component';
import { EditKatalogitemComponent } from './edit-katalogitem/edit-katalogitem.component';
import { CommonSchulkatalogModule } from '@minikaenguru-ws/common-schulkatalog';

@NgModule({
	imports: [
		CommonModule,
		ReactiveFormsModule,
		KatalogpflegeRoutingModule,
		CommonSchulkatalogModule
		// StoreModule.forFeature(fromWettbewerbe.wettbewerbeFeatureKey, fromWettbewerbe.reducer),
		// EffectsModule.forFeature([WettbewerbeEffects])
	],
	declarations: [
		KatalogpflegeComponent,
		EditOrtComponent,
		EditLandComponent,
		EditSchuleComponent,
		EditKatalogitemComponent
	],
	exports: [
		],
	providers: [
	]
})
export class KatalogpflegeModule {}
