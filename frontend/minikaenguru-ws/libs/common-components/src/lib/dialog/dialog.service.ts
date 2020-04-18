import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';


@Injectable({
	providedIn: 'root'
})
export class DialogService {

	private subject = new Subject();

	close$: Observable<any> = this.subject.asObservable();

	constructor() { }

	close() {
		this.subject.next('hideDialog');
	}

	open() {
		this.subject.next('showDialog');
	}
}
