import { Component, Input, OnInit } from '@angular/core';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { UploadMonitoringInfo } from '../uploads.model';

@Component({
  selector: 'mka-uploads-card',
  templateUrl: './uploads-card.component.html',
  styleUrls: ['./uploads-card.component.css']
})
export class UploadsCardComponent implements OnInit {

  devMode = environment.envName === 'DEV';

  @Input()
  uploadInfo!: UploadMonitoringInfo;

  constructor() { }

  ngOnInit() {
  }

}
