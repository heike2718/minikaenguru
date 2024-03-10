import { Inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Message } from '../domain/entities';
import { DOCUMENT } from '@angular/common';

@Injectable({
	providedIn: 'root'
})
export class MessageService {

	private messageSubject = new BehaviorSubject<Message | undefined>(undefined);

	message$: Observable<Message | undefined> = this.messageSubject.asObservable();

	constructor(@Inject(DOCUMENT) private document: Document) { }

	showMessage(message: Message) {

		switch (message.level) {
			case 'INFO': this.info(message.message);
				break;
			case 'WARN': this.warn(message.message);
				break;
			case 'ERROR': this.error(message.message);
				break;
		}
		this.#scrollToTop();
	}

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

	#scrollToTop() {
        const document = this.document;
        (function smoothscroll() {
            const currentScroll = document.documentElement.scrollTop || document.body.scrollTop;
            // console.log('currentScroll=' + currentScroll);
            if (currentScroll > 0) {
                window.requestAnimationFrame(smoothscroll);
                window.scrollTo(0, currentScroll - (currentScroll / 8));
            }
        })();
    }
}
