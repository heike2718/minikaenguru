import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { MustertexteFacade } from '../mustertexte.facade';

@Component({
  selector: 'mka-mustertext-list',
  templateUrl: './mustertext-list.component.html',
  styleUrls: ['./mustertext-list.component.css']
})
export class MustertextListComponent implements OnInit {

  devMode = environment.envName === 'DEV'

  constructor(public mustertextFacade: MustertexteFacade, 
    private router: Router) { }

  ngOnInit(): void {
  }


  public loadMustertexte(): void {

    this.mustertextFacade.loadMustertexte();

  }

  addMustertext(): void {
		//
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}

 


}
