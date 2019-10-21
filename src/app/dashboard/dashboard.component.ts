import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { environment } from 'src/environments/environment';
import { LogService } from 'hewi-ng-lib';

@Component({
	selector: 'mkadm-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

	pathLogo = environment.assetsUrl + '/mja_logo.png';

	/** Based on the screen size, switch from standard to one column per row */
	cards = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
		map(({ matches }) => {
			if (matches) {
				return [
					{ title: 'Card 1', cols: 1, rows: 1 },
					{ title: 'Card 2', cols: 1, rows: 1 },
					{ title: 'Card 3', cols: 1, rows: 1 },
					{ title: 'Card 4', cols: 1, rows: 1 }
				];
			}

			return [
				{ title: 'Card 1', cols: 2, rows: 1 },
				{ title: 'Card 2', cols: 1, rows: 1 },
				{ title: 'Card 3', cols: 1, rows: 2 },
				{ title: 'Card 4', cols: 1, rows: 1 }
			];
		})
	);

	constructor(private breakpointObserver: BreakpointObserver, private logger: LogService) { }

	ngOnInit() {
		this.logger.debug('[Dashboard Component] initialized', null);
	}
}
