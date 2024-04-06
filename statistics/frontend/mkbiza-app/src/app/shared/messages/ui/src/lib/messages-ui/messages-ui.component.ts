import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService } from '@mkbiza-app/messages-api';
import { Subscription, debounceTime, tap } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'mkbiza-message',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './messages-ui.component.html',
  styleUrl: './messages-ui.component.scss',
})
export class MessagesUiComponent implements OnInit, OnDestroy {

  messageService = inject(MessageService);

  #messageSubscription: Subscription = new Subscription();

  ngOnInit(): void {

    this.#messageSubscription = this.messageService.message$.pipe(
      debounceTime(3000),
      tap((message) => {
        if (message && message.level === 'INFO') {
          this.close();
        }
      })

    ).subscribe();
  }

  ngOnDestroy(): void {
    this.#messageSubscription.unsubscribe();
  }

  close(): void {
    this.messageService.clear();
  }

}
