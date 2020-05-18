import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SchuleDashboardComponent } from './schule-dashboard/schule-dashboard.component';
import { SchulenListComponent } from './schulen-list/schulen-list.component';
import { SchuleCardComponent } from './schule-card/schule-card.component';
import { SchulenRoutingModule } from './schulen-routing.module';



@NgModule({
	imports: [
		CommonModule,
		SchulenRoutingModule
	],
	declarations: [
		SchulenListComponent,
		SchuleDashboardComponent,
		SchuleCardComponent],
	exports: [
		SchulenListComponent
	]
})
export class SchulenModule { }
