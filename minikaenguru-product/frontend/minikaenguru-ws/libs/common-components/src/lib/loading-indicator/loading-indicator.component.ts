import { Component, OnInit } from '@angular/core';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Component({
  selector: 'cmn-loading-indicator',
  templateUrl: './loading-indicator.component.html',
  styleUrls: ['./loading-indicator.component.css'],
})
export class LoadingIndicatorComponent implements OnInit {

  constructor(public loadingIndicatorService: LoadingIndicatorService) { }

  ngOnInit(): void { }
}
