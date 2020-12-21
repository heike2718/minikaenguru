import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { VeranstalterGuardService } from '../infrastructure/veranstalter-guard.service';
import { LoesungszettelEditorComponent } from './loesungszettel-editor/loesungszettel-editor.component';
import { LoesungszettelEditorResolver } from './loesungszettel-editor/loesungszettel-editor.resolver';

const loesungszettelRoutes: Routes = [

	{
		path: 'loesungszettel',
		canActivate: [VeranstalterGuardService],
		// resolve: LoesungszettelEditorResolver,
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
