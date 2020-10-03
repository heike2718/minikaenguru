import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { NgModule } from '@angular/core';
import { VeranstalterListComponent } from './veranstalter-list/veranstalter-list.component';


const veranstalterRoutes: Routes = [

	{
		path: 'veranstalter',
		canActivate: [AuthGuardService],
		component: VeranstalterListComponent
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
