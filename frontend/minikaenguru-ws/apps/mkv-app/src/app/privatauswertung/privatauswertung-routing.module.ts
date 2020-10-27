import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { PrivatveranstalterGuardService } from '../infrastructure/privatveranstalter-guard.service';
import { KinderListComponent } from './kinder-list/kinder-list.component';
import { KinderListResolver } from './kinder-list/kinder-list.resolver';


const privatauswertungRoutes: Routes = [

	{
		path: 'privatauswertung',
		canActivate: [PrivatveranstalterGuardService],
		resolve: {kinderList: KinderListResolver},
		component: KinderListComponent
	}


];

@NgModule({
	imports: [
		RouterModule.forChild(privatauswertungRoutes)
	],
	exports: [
		RouterModule
	]
})
export class PrivatauswertungRoutingModule {}
