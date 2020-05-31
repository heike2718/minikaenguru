import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';

import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { WettbewerbeListComponent } from './wettbewerbe-list/wettbewerbe-list.component';
import { WettbewerbeListResolver } from './wettbewerbe-list/wettbewerbe-list.resolver';
import { WettbewerbDashboardComponent } from './wettbewerb-dashboard/wettbewerb-dashboard.component';
import { WettbewerbDetailsResolver } from './wettbewerb-details.resolver';
import { WettbewerbEditorComponent } from './wettbewerb-editor/wettbewerb-editor.component';

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
		resolve: {wettbewerbDashboard: WettbewerbDetailsResolver},
		component: WettbewerbDashboardComponent
	},
	{
		path: 'wettbewerb-editor/:id',
		canActivate: [AuthGuardService],
		resolve: {wettbewerbEditor: WettbewerbDetailsResolver},
		component: WettbewerbEditorComponent
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
