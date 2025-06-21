import { Component, Input, OnInit } from '@angular/core';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { Loesungszettel } from '../loesungszettel.model';

@Component({
  selector: 'mka-loesungszettel-card',
  templateUrl: './loesungszettel-card.component.html',
  styleUrls: ['./loesungszettel-card.component.css']
})
export class LoesungszettelCardComponent implements OnInit {

  devMode = environment.envName === 'DEV';

  @Input()
  loesungszettel!: Loesungszettel;

  constructor() { }

  ngOnInit() {
  }

}
