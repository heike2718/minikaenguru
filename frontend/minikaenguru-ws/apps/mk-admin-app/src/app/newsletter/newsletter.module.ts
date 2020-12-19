import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsletterListComponent } from './newsletter-list/newsletter-list.component';
import { NewsletterCardComponent } from './newsletter-card/newsletter-card.component';
import { EditNewsletterComponent } from './edit-newsletter/edit-newsletter.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';

import * as fromNewsletters from './+state/newsletter.reducer';
import { NewsletterRoutingModule } from './newsletter-routing.module';
import { BrowserModule } from '@angular/platform-browser';



@NgModule({
  declarations: [
	  NewsletterListComponent,
	  NewsletterCardComponent,
	  EditNewsletterComponent],
  imports: [
	CommonModule,
	BrowserModule,
	FormsModule,
	ReactiveFormsModule,
	NewsletterRoutingModule,
	StoreModule.forFeature(fromNewsletters.newsletterFeatureKey, fromNewsletters.reducer)
  ]
})
export class NewsletterModule { }
