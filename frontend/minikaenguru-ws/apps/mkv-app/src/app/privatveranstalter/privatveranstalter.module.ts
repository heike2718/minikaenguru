import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PrivatDashboardComponent } from './privat-dashboard/privat-dashboard.component';
import { StoreModule } from '@ngrx/store';
import * as fromPrivat from './+state/privatveranstalter.reducer';
import { PrivatveranstalterRoutingModule } from './privatveranstalter-routing.module';
import { PrivatDashboardResolver } from './privat-dashboard/privat-dashboard.resolver';



@NgModule({
	imports: [
		CommonModule,
		StoreModule.forFeature(fromPrivat.privatveranstalterFeatureKey, fromPrivat.reducer),
		PrivatveranstalterRoutingModule
	],
	declarations: [
		PrivatDashboardComponent,
	],
	exports: [
		PrivatDashboardComponent
	],
	providers: [
		PrivatDashboardResolver
	]
})
export class PrivatveranstalterModule { }
