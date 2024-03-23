import { Component, Input, OnInit } from '@angular/core';
import { StatistikGruppenitem } from '../statistik.model';

@Component({
  selector: 'mka-statistik-item',
  templateUrl: './statistik-item.component.html',
  styleUrls: ['./statistik-item.component.css']
})
export class StatistikItemComponent implements OnInit {

  @Input()
  statistikItem!: StatistikGruppenitem;

  constructor() { }

  ngOnInit(): void {
  }

}
