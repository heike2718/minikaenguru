import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'mkv-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  collapsed = true;

  constructor() { }

  ngOnInit() {
  }

}
