import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { WettbewerbeFacade } from '../wettbewerbe.facade';

@Component({
  selector: 'mkod-wettbewerbe-list',
  templateUrl: './wettbewerbe-list.component.html',
  styleUrls: ['./wettbewerbe-list.component.css']
})
export class WettbewerbeListComponent implements OnInit {

  constructor(public wettbewerbeFacade: WettbewerbeFacade, private router: Router) { }

  ngOnInit(): void {

    this.wettbewerbeFacade.clearWettbewerbSelection();
  }

  gotoLanding(): void {
    this.router.navigateByUrl('/landing');
  }

  gotoAnmeldungen(): void {
    this.router.navigateByUrl('/anmeldungen');
  }

}
