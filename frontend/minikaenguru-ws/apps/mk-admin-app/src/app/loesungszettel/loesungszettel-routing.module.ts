import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { LoesungszettelListComponent } from './loesungszettel-list/loesungszettel-list.component';


const loesungszettelRoutes: Routes = [

    {
		path: 'loesungszettel',
		canActivate: [AuthGuardService],
		component: LoesungszettelListComponent
	},

];

@NgModule({
	imports: [
		RouterModule.forChild(loesungszettelRoutes)
	],
	exports: [
		RouterModule
	]
})
export class LoesungszettelRoutingModule {}
