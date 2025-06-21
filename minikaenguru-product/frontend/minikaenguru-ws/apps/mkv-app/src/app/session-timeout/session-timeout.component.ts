import { Component, OnInit } from '@angular/core';
import { LogoutService } from '../services/logout.service';
import { MessageService, Message } from '@minikaenguru-ws/common-messages';
import { STORAGE_KEY_INVALID_SESSION } from '@minikaenguru-ws/common-auth';

@Component({
	selector: 'mkv-session-timeout',
	templateUrl: './session-timeout.component.html',
	styleUrls: ['./session-timeout.component.css']
})
export class SessionTimeoutComponent implements OnInit {

	constructor(private logoutService: LogoutService, private messageService: MessageService) { }

	ngOnInit() {


		const sessionMessage = localStorage.getItem(STORAGE_KEY_INVALID_SESSION);


		this.logoutService.logout();

		if (sessionMessage) {

			const message = JSON.parse(sessionMessage) as Message;
			this.messageService.showMessage(message);

			localStorage.removeItem(STORAGE_KEY_INVALID_SESSION);
		}
	}
}
