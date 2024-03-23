import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { VeranstalterGuardService } from '../infrastructure/veranstalter-guard.service';
import { UrkundenauftragComponent } from './urkundenauftrag/urkundenauftrag.component';
import { LehrerGuardService } from '../infrastructure/lehrer-guard.service';
import { AuswertungsaufragComponent } from './auswertungsaufrag/auswertungsaufrag.component';

const urkundenRoutes: Routes = [
	{
		path: 'einzelurkunden',
		canActivate: [VeranstalterGuardService],
		component: UrkundenauftragComponent
	},
	{
		path: 'schulauswertung',
		canActivate: [LehrerGuardService],
		component: AuswertungsaufragComponent
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
