import { Component } from '@angular/core';
import { NewsletterFacade } from '../newsletter/newsletter.facade';

@Component({
	selector: 'mka-versandinfo-progress',
	templateUrl: './versandinfo-progress.component.html',
	styleUrls: ['./versandinfo-progress.component.css']
})
export class VersandinfoProgressComponent {

	constructor(public newsletterFacade: NewsletterFacade) { }

}
