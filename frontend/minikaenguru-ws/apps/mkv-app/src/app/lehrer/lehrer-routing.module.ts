import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { SchulenListComponent } from './schulen/schulen-list/schulen-list.component';
import { LehrerGuardService } from '../infrastructure/lehrer-guard.service';
import { LehrerDashboardComponent } from './lehrer-dashboard/lehrer-dashboard.component';
import { LehrerDashboardResolver } from './lehrer-dashboard/lehrer-dashboard.resolver';
import { SchuleDashboardComponent } from './schulen/schule-dashboard/schule-dashboard.component';
import { SchuleDashboardResolver } from './schulen/schule-dashboard/schule-dashboard.resolver';
import { SchulenListResolver } from './schulen/schulen-list/schulen-list.resolver';
import { AddSchuleComponent } from './schulen/add-schule/add-schule.component';


const lehrerRoutes: Routes = [
	{
		path: 'lehrer',
		children: [
			{
				path: 'dashboard',
				canActivate: [LehrerGuardService],
				resolve: { lehrerdashboard: LehrerDashboardResolver },
				component: LehrerDashboardComponent,
			}, {
				path: 'schulen',
				canActivate: [LehrerGuardService],
				component: SchulenListComponent,
				resolve: {schulen: SchulenListResolver }
			}, {
				path: 'schulen/add-schule',
				canActivate: [LehrerGuardService],
				component: AddSchuleComponent
			}, {
				path: 'schule-dashboard/:kuerzel',
				canActivate: [LehrerGuardService],
				resolve: { schuleDashboard: SchuleDashboardResolver },
				component: SchuleDashboardComponent
			}
		]
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(lehrerRoutes)
	],
	exports: [
		RouterModule
	]
})
export class LehrerRoutingModule {

}
