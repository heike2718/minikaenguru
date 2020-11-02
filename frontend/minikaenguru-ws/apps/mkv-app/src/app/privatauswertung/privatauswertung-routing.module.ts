import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { PrivatveranstalterGuardService } from '../infrastructure/privatveranstalter-guard.service';
import { KinderListComponent } from './kinder-list/kinder-list.component';
import { KinderListResolver } from './kinder-list/kinder-list.resolver';
import { KindEditorResolver } from './kind-editor/kind-editor.resolver';
import { KindEditorComponent } from './kind-editor/kind-editor.component';


const privatauswertungRoutes: Routes = [

	{
		path: 'privatauswertung/:teilnahmenummer',
		canActivate: [PrivatveranstalterGuardService],
		resolve: {kinderList: KinderListResolver},
		component: KinderListComponent
	},
	{
		path: 'kind-editor/:id',
		canActivate: [PrivatveranstalterGuardService],
		resolve: {kindEditor: KindEditorResolver},
		component: KindEditorComponent
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
