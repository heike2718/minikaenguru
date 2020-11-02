import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { PrivatveranstalterGuardService } from '../infrastructure/privatveranstalter-guard.service';
import { KinderListComponent } from './kinder-list/kinder-list.component';
import { KinderListResolver } from './kinder-list/kinder-list.resolver';
import { KindEditorResolver } from './kind-editor/kind-editor.resolver';
import { KindEditorComponent } from './kind-editor/kind-editor.component';
import { LehrerGuardService } from '../infrastructure/lehrer-guard.service';
import { VeranstalterGuardService } from '../infrastructure/veranstalter-guard.service';


const kinderRoutes: Routes = [

	{
		path: 'kinder/:teilnahmenummer',
		canActivate: [VeranstalterGuardService],
		resolve: {kinderList: KinderListResolver},
		component: KinderListComponent
	},
	{
		path: 'kind-editor/:id',
		canActivate: [VeranstalterGuardService],
		resolve: {kindEditor: KindEditorResolver},
		component: KindEditorComponent
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(kinderRoutes)
	],
	exports: [
		RouterModule
	]
})
export class KinderRoutingModule {}
