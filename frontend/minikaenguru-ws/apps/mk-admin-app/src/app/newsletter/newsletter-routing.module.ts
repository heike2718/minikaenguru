import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../infrastructure/auth-guard.service';
import { NewsletterListComponent } from './newsletter-list/newsletter-list.component';
import { EditNewsletterComponent } from './edit-newsletter/edit-newsletter.component';
import { NgModule } from '@angular/core';


const newslettersRoutes: Routes = [

	{
		path: 'newsletters',
		canActivate: [AuthGuardService],
		component: NewsletterListComponent
	},
	{
		path: 'newsletter-editor/:id',
		canActivate: [AuthGuardService],
		component: EditNewsletterComponent
	}
];


@NgModule({
	imports: [
		RouterModule.forChild(newslettersRoutes)
	],
	exports: [
		RouterModule
	]
})
export class NewsletterRoutingModule {}
