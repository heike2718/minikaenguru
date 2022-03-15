import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { NgModule } from '@angular/core';
import { MustertextListComponent } from './mustertext-list/mustertext-list.component';
import { EditMustertextComponent } from './edit-mustertext/edit-mustertext.component';


const mustertexteRoutes: Routes = [

	{
		path: 'mustertexte',
		canActivate: [AuthGuardService],
		component: MustertextListComponent
	},
	{
		path: 'mustertext-editor/:id',
		canActivate: [AuthGuardService],
		component: EditMustertextComponent
	}
];


@NgModule({
	imports: [
		RouterModule.forChild(mustertexteRoutes)
	],
	exports: [
		RouterModule
	]
})
export class MustertexteRoutingModule {}
