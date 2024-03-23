import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { NgModule } from '@angular/core';
import { StatistikDashboardComponent } from './statistik-dashboard/statistik-dashboard.component';
import { StatistikResolver } from './+state/statistik.resolver';

const statistikRoutes: Routes = [

    {
        path: 'statistik',
        canActivate: [AuthGuardService],
        component: StatistikDashboardComponent,
		resolve: {statistik: StatistikResolver}
    }

];

@NgModule({
	imports: [
		RouterModule.forChild(statistikRoutes)
	],
	exports: [
		RouterModule
	]
})
export class StatistikRoutingModule{}

