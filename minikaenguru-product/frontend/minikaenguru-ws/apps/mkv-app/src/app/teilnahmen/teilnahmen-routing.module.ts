import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { VeranstalterGuardService } from '../infrastructure/veranstalter-guard.service';
import { TeilnahmenListComponent } from './teilnahmen-list/teilnahmen-list.component';
import { TeilnahmenListResolver } from './teilnahmen-list/teilnahmen-list.resolver';

const teilnahmenRoutes: Routes = [
	{
		path: 'teilnahmen',
		children: [
			{
				path: '',
				canActivate: [VeranstalterGuardService],
				resolve: {teilnahmenList: TeilnahmenListResolver},
				component: TeilnahmenListComponent,

			}
		]
	}
];

@NgModule({
	imports: [
		RouterModule.forChild(teilnahmenRoutes)
	],
	exports: [
		RouterModule
	]
})
export class TeilnahmenRoutingModule {

}
