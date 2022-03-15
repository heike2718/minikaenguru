import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsletterListComponent } from './newsletter-list/newsletter-list.component';
import { NewsletterCardComponent } from './newsletter-card/newsletter-card.component';
import { EditNewsletterComponent } from './edit-newsletter/edit-newsletter.component';
import { ReactiveFormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';

import * as fromNewsletters from './+state/newsletter.reducer';
import { NewsletterRoutingModule } from './newsletter-routing.module';



@NgModule({
  declarations: [
	  NewsletterListComponent,
	  NewsletterCardComponent,
	  EditNewsletterComponent],
  imports: [
	CommonModule,
	ReactiveFormsModule,
	NewsletterRoutingModule,
	StoreModule.forFeature(fromNewsletters.newsletterFeatureKey, fromNewsletters.reducer)
  ]
})
export class NewsletterModule { }
