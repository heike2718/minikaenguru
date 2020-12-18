import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsletterListComponent } from './newsletter-list/newsletter-list.component';
import { NewsletterCardComponent } from './newsletter-card/newsletter-card.component';
import { EditNewsletterComponent } from './edit-newsletter/edit-newsletter.component';



@NgModule({
  declarations: [NewsletterListComponent, NewsletterCardComponent, EditNewsletterComponent],
  imports: [
    CommonModule
  ]
})
export class NewsletterModule { }
