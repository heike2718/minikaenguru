import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { VersandauftraegeListComponent } from './versandauftraege-list/versandauftraege-list.component';
import { NgModule } from '@angular/core';


const versandauftraegeRoutes: Routes = [

	{
		path: 'versandauftraege',
		canActivate: [AuthGuardService],
		component: VersandauftraegeListComponent
	}
];


@NgModule({
	imports: [
		RouterModule.forChild(versandauftraegeRoutes)
	],
	exports: [
		RouterModule
	]
})
export class VersandauftraegeRoutingModule {}
