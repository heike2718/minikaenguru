import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { LehrerGuardService } from '../infrastructure/lehrer-guard.service';
import { KlassenListComponent } from './klassen-list/klassen-list.component';
import { KlasseEditorComponent } from './klasse-editor/klasse-editor.component';
import { UploadKlassenlistenComponent } from './upload-klassenlisten/upload-klassenlisten.component';



const klassenRoutes: Routes = [

	{
		path: 'klassen/:schulkuerzel',
		canActivate: [LehrerGuardService],
		component: KlassenListComponent
	},
	{
		path: 'klasse-editor/:id',
		canActivate: [LehrerGuardService],
		component: KlasseEditorComponent
	},
	{
		path: 'klassen/uploads/:schulkuerzel',
		canActivate: [LehrerGuardService],
		component: UploadKlassenlistenComponent
	},

];


@NgModule({
	imports: [
		RouterModule.forChild(klassenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class KlassenRoutingModule {}
