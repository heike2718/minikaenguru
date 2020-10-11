import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { EventlogComponent } from './eventlog.component';



const eventlogRoutes: Routes = [

	{
		path: 'eventlog',
		canActivate: [AuthGuardService],
		component: EventlogComponent
	}

];


@NgModule({
	imports: [
		RouterModule.forChild(eventlogRoutes)
	],
	exports: [
		RouterModule
	]
})
export class EventlogRoutingModule { }
