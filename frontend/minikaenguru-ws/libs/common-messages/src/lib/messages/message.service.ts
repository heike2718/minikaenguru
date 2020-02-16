import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Message } from '../domain/entities';

@Injectable({
	providedIn: 'root'
})
export class MessageService {

	private messageSubject = new BehaviorSubject<Message>(undefined);

	message$: Observable<Message> = this.messageSubject.asObservable();

	constructor() { }

	info(message: string) {
		this.messageSubject.next({
			level: 'INFO',
			message: message
		});
	}

	warn(message: string) {
		this.messageSubject.next({
			level: 'WARN',
			message: message
		});
	}

	error(message: string) {
		this.messageSubject.next({
			level: 'ERROR',
			message: message
		});
	}

	clear() {
		this.messageSubject.next(undefined);
	}
}
