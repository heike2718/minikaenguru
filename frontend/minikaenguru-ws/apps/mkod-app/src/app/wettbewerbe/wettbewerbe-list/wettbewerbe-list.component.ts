import { Component, OnInit } from '@angular/core';
import { WettbewerbeFacade } from '../wettbewerbe.facade';

@Component({
  selector: 'mkod-wettbewerbe-list',
  templateUrl: './wettbewerbe-list.component.html',
  styleUrls: ['./wettbewerbe-list.component.css']
})
export class WettbewerbeListComponent implements OnInit {

  constructor(public wettbewerbeFacade: WettbewerbeFacade) { }

  ngOnInit(): void {

    this.wettbewerbeFacade.clearWettbewerbSelection();
  }

}
