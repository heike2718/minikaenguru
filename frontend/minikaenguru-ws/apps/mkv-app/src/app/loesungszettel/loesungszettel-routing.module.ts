import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { VeranstalterGuardService } from '../infrastructure/veranstalter-guard.service';
import { LoesungszettelEditorComponent } from './loesungszettel-editor/loesungszettel-editor.component';

const loesungszettelRoutes: Routes = [

	{
		path: 'loesungszettel',
		canActivate: [VeranstalterGuardService],
		component: LoesungszettelEditorComponent
	}

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
