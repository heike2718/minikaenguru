import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';

import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { WettbewerbeListComponent } from './wettbewerbe-list/wettbewerbe-list.component';
import { WettbewerbeListResolver } from './wettbewerbe-list/wettbewerbe-list.resolver';
import { WettbewerbDashboardComponent } from './wettbewerb-dashboard/wettbewerb-dashboard.component';
import { WettbewerbDashboardResolver } from './wettbewerb-dashboard/wettbewerb-dashboard.resolver';

const wettbewerbeRoutes: Routes = [

	{
		path: 'wettbewerbe',
		canActivate: [AuthGuardService],
		resolve: { wettbewerbe: WettbewerbeListResolver },
		component: WettbewerbeListComponent
	},
	{
		path: 'wettbewerb-dashboard/:id',
		canActivate: [AuthGuardService],
		resolve: {wettbewerbDashboard: WettbewerbDashboardResolver},
		component: WettbewerbDashboardComponent
	}
];


@NgModule({
	imports: [
		RouterModule.forChild(wettbewerbeRoutes)
	],
	exports: [
		RouterModule
	]
})
export class WettbewerbeRoutingModule { }
