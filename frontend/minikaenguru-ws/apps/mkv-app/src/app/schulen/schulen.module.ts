import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SchuleDashboardComponent } from './schule-dashboard/schule-dashboard.component';
import { SchulenListComponent } from './schulen-list/schulen-list.component';
import { SchuleCardComponent } from './schule-card/schule-card.component';
import { SchulenRoutingModule } from './schulen-routing.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import * as fromSchulen from './+state/schulen.reducer';
import { SchulenListResolver } from './schulen-list/schulen-list.resolver';



@NgModule({
	imports: [
		CommonModule,
		SchulenRoutingModule,
		StoreModule.forFeature(fromSchulen.schulenFeatureKey, fromSchulen.reducer),
		EffectsModule.forFeature([])
	],
	declarations: [
		SchulenListComponent,
		SchuleDashboardComponent,
		SchuleCardComponent],
	exports: [
		SchulenListComponent
	],
	providers: [
		SchulenListResolver
	]
})
export class SchulenModule { }
