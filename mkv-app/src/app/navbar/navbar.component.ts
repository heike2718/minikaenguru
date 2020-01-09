import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'mkv-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  collapsed = true;

  constructor(private authService: AuthService) { }

  ngOnInit() {
  }

  login() {

    this.authService.startLogin();

  }
}
