import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { KatalogpflegeRoutingModule } from './katalogpflege-routing.module';
import { KatalogpflegeComponent } from './katalogpflege.component';
import * as fromKatalogpflege from './+state/katalogpflege.reducer';
import { KatalogpflegeFacade } from './katalogpflege.facade';
import { LaenderListComponent } from './laender/laender-list/laender-list.component';
import { OrteListComponent } from './orte/orte-list/orte-list.component';
import { SchulenListComponent } from './schulen/schulen-list/schulen-list.component';
import { LaenderListResolver } from './laender/laender-list/laender-list.resolver';
import { EditOrtComponent } from './orte/edit-ort/edit-ort.component';
import { EditLandComponent } from './laender/edit-land/edit-land.component';
import { EditSchuleComponent } from './schulen/edit-schule/edit-schule.component';
import { KatalogpflegeItemComponent } from './katalogpflege-item/katalogpflege-item.component';

@NgModule({
	imports: [
		CommonModule,
		FormsModule,
		ReactiveFormsModule,
		KatalogpflegeRoutingModule,
		StoreModule.forFeature(fromKatalogpflege.katalogpflegeFeatureKey, fromKatalogpflege.reducer),
	],
	declarations: [
		KatalogpflegeComponent,
		EditOrtComponent,
		EditLandComponent,
		EditSchuleComponent,
		LaenderListComponent,
		OrteListComponent,
		SchulenListComponent,
		KatalogpflegeItemComponent
	],
	exports: [
		],
	providers: [
		KatalogpflegeFacade,
		LaenderListResolver
	]
})
export class KatalogpflegeModule {}
