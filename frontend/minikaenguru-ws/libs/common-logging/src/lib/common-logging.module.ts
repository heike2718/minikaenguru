import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { LogService } from './log.service';
import { LogPublisher } from './domain/log-publishers';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule
  ]
})
export class CommonLoggingModule {}
