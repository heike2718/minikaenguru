import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { UploadsListComponent } from './uploads-list/uploads-list.component';

const uploadsRoutes: Routes = [

    {
		path: 'uploads',
		canActivate: [AuthGuardService],
		component: UploadsListComponent
	},

];

@NgModule({
	imports: [
		RouterModule.forChild(uploadsRoutes)
	],
	exports: [
		RouterModule
	]
})
export class UploadsRoutingModule {}

