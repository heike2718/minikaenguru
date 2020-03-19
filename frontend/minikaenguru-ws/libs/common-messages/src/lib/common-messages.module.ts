import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessagesComponent } from './messages/message.component';
import { MessageService } from './messages/message.service';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    MessagesComponent],
  exports: [
    MessagesComponent
  ]
})
export class CommonMessagesModule {}
