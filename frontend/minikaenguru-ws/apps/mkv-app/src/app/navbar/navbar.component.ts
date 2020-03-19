import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'mkv-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  collapsed = true;

  constructor() { }

  ngOnInit() {
  }

  login() {
    // mal noch nichts
  }

}
