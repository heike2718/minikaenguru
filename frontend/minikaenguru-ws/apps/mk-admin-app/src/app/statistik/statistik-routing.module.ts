import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { NgModule } from '@angular/core';
import { StatistikDashboardComponent } from './statistik-dashboard/statistik-dashboard.component';

const statistikRoutes: Routes = [

    {
        path: 'statistik',
        canActivate: [AuthGuardService],
        component: StatistikDashboardComponent
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

