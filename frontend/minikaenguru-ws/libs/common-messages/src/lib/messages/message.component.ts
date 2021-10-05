import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Message } from '../domain/entities';
import { Subscription } from 'rxjs';
import { MessageService } from './message.service';

@Component({
  selector: 'cmn-msg',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessagesComponent implements OnInit, OnDestroy {

  @Input() escape = true;

  msg?: Message;

	private subscription: Subscription = new Subscription();

  constructor(private messageService: MessageService) { }

  ngOnInit() {

    this.subscription = this.messageService.message$.subscribe(
      message => this.msg = message
    );

  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  close() {
    this.messageService.clear();
  }

  getClasses() {
		const result = [];
		result.push('alert');

    if (this.msg) {

		  switch (this.msg.level) {
			  case 'INFO': result.push('alert-info'); break;
			  case 'WARN': result.push('alert-warning'); break;
			  case 'ERROR': result.push('alert-danger'); break;
		  }
    }
		return result;
	}
}
