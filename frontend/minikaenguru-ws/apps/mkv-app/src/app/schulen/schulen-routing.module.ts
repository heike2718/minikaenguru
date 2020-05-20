import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { SchulenListComponent } from './schulen-list/schulen-list.component';
import { SchuleDashboardComponent } from './schule-dashboard/schule-dashboard.component';
import { SchulenListResolver } from './schulen-list/schulen-list.resolver';
import { AuthGuardService } from '../infrastructure/auth-guard.service';


const schulenRoutes: Routes = [
	{
		path: 'schulen',
		canActivate: [AuthGuardService],
	  	resolve: {schulen: SchulenListResolver},
		component: SchulenListComponent,
		data: { animation: 'schulen' }
	}, {
		path: 'schule-dashboard',
		component: SchuleDashboardComponent,
		data: { animation: 'schule-dashboard' }
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(schulenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class SchulenRoutingModule {

}
