import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';

import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { EditAktuelleMeldungComponent } from './edit-aktuelle-meldung.component';

const aktuelleMeldungRoutes: Routes = [

	{
		path: 'aktuelle-meldung',
		canActivate: [AuthGuardService],
		component: EditAktuelleMeldungComponent,
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(aktuelleMeldungRoutes)
	],
	exports: [
		RouterModule
	]
})
export class AktuelleMeldungenRoutingModule {}
