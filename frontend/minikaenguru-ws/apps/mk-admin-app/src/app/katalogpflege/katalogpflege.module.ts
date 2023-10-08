import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { KatalogpflegeRoutingModule } from './katalogpflege-routing.module';
import { KatalogpflegeComponent } from './katalogpflege.component';
import * as fromKatalogpflege from './+state/katalogpflege.reducer';
import { KatalogpflegeFacade } from './katalogpflege.facade';
import { DeprecatedLaenderListComponent } from './laender/laender-list/deprecated-laender-list.component';
import { OrteListComponent } from './orte/orte-list/orte-list.component';
import { SchulenListComponent } from './schulen/schulen-list/schulen-list.component';
import { DeprecatedLaenderListResolver } from './laender/laender-list/deprecadet-laender-list.resolver';
import { EditOrtComponent } from './orte/edit-ort/edit-ort.component';
import { EditLandComponent } from './laender/edit-land/edit-land.component';
import { EditSchuleComponent } from './schulen/edit-schule/edit-schule.component';
import { KatalogpflegeItemComponent } from './katalogpflege-item/katalogpflege-item.component';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';

@NgModule({
	imports: [
		CommonModule,
		FormsModule,
		ReactiveFormsModule,
		CommonComponentsModule,
		KatalogpflegeRoutingModule,
		StoreModule.forFeature(fromKatalogpflege.katalogpflegeFeatureKey, fromKatalogpflege.reducer),
	],
	declarations: [
		KatalogpflegeComponent,
		EditOrtComponent,
		EditLandComponent,
		EditSchuleComponent,
		DeprecatedLaenderListComponent,
		OrteListComponent,
		SchulenListComponent,
		KatalogpflegeItemComponent
	],
	exports: [],
	providers: [
		KatalogpflegeFacade,
		DeprecatedLaenderListResolver
	]
})
export class KatalogpflegeModule {}
