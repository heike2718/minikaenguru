import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { NgModule } from '@angular/core';
import { VeranstalterComponent } from './veranstalter/veranstalter.component';
import { VeranstalterDetailsComponent } from './veranstalter-details/veranstalter-details.component';


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
	}

]

@NgModule({
	imports: [
		RouterModule.forChild(veranstalterRoutes)
	],
	exports: [
		RouterModule
	]
})
export class VeranstalterRoutingModule {}
