import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { PrivatveranstalterGuardService } from '../infrastructure/privatveranstalter-guard.service';
import { PrivatDashboardComponent } from './privat-dashboard/privat-dashboard.component';
import { PrivatDashboardResolver } from './privat-dashboard/privat-dashboard.resolver';


const privatveranstalterRoutes: Routes = [
	{
		path: 'privat',
		children: [
			{
				path: 'dashboard',
				canActivate: [PrivatveranstalterGuardService],
				resolve: { privatdashboard: PrivatDashboardResolver },
				component: PrivatDashboardComponent,
			}
		]
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(privatveranstalterRoutes)
	],
	exports: [
		RouterModule
	]
})
export class PrivatveranstalterRoutingModule {

}
