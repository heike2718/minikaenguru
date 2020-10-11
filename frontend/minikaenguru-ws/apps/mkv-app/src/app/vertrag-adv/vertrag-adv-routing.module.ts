import { Routes, RouterModule } from '@angular/router';
import { LehrerGuardService } from '../infrastructure/lehrer-guard.service';
import { VertragAdvResolver } from './vertrag-avd.resolver';
import { VertragAdvComponent } from './vertrag-adv.component';
import { NgModule } from '@angular/core';


const vertragAdvRoutes: Routes = [

	{
		path: 'adv',
		children: [
			{
				path: '',
				canActivate: [LehrerGuardService],
				resolve: { vertragEditor: VertragAdvResolver },
				component: VertragAdvComponent
			}
		]
	}
];


@NgModule({
	imports: [
		RouterModule.forChild(vertragAdvRoutes)
	],
	exports: [
		RouterModule
	]
})
export class VertragAdvRoutingModule { }
