import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { VeranstalterGuardService } from '../infrastructure/veranstalter-guard.service';
import { UrkundenauftragComponent } from './urkundenauftrag/urkundenauftrag.component';

const urkundenRoutes: Routes = [
	{
		path: 'urkunden',
		canActivate: [VeranstalterGuardService],
		component: UrkundenauftragComponent
	}
]


@NgModule({
	imports: [
		RouterModule.forChild(urkundenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class UrkundenRoutingModule {}
