import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { NgModule } from '@angular/core';
import { VeranstalterComponent } from './veranstalter/veranstalter.component';
import { VeranstalterDetailsComponent } from './veranstalter-details/veranstalter-details.component';
import { PrivatteilnahmeOverviewComponent } from './privatteilnahme-overview/privatteilnahme-overview.component';


const veranstalterRoutes: Routes = [

	{
		path: 'veranstalter',
		canActivate: [AuthGuardService],
		component: VeranstalterComponent
	},
	{
		path: 'details',
		canActivate: [AuthGuardService],
		component: VeranstalterDetailsComponent
	},
	{
		path: 'privatteilnahme',
		canActivate: [AuthGuardService],
		component: PrivatteilnahmeOverviewComponent
	}

];

@NgModule({
	imports: [
		RouterModule.forChild(veranstalterRoutes)
	],
	exports: [
		RouterModule
	]
})
export class VeranstalterRoutingModule { }
