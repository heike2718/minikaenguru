import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { NgModule } from '@angular/core';
import { SchuleOverviewComponent } from './schule-overview/schule-overview.component';

const schulteilnahmenRoutes: Routes = [

	{
		path: 'schulteilnahme',
		canActivate: [AuthGuardService],
		component: SchuleOverviewComponent
	}

];

@NgModule({
	imports: [
		RouterModule.forChild(schulteilnahmenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class SchulteilnahmenRoutingModule { }
