import { Component, OnInit } from '@angular/core';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {


	user$ = this.authService.user$;

	constructor(private authService: AuthService, private router: Router) { }

	ngOnInit(): void {

		this.user$.subscribe(
			user => {

				if (user) {
					if (user.rolle) {
						if (user.rolle === 'LEHRER') {
							this.router.navigateByUrl('/lehrer/dashboard');
						} else {
							if (user.rolle === 'PRIVAT') {
								this.router.navigateByUrl('/privat/dashboard');
							} else {
								this.router.navigateByUrl('');
							}
						}
					}
				} else {
					this.router.navigateByUrl('');
				}
			}
		)
	}
}
