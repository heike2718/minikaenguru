import { Component, OnInit } from '@angular/core';
import * as fromAppState from './reducers/index';
import { Store } from '@ngrx/store';
import { AuthService } from './auth/auth.service';
import { JWTService, AuthResult } from 'hewi-ng-lib';
import { tap } from 'rxjs/operators';
import { noop } from 'rxjs';
import * as AuthActions from './auth/auth.actions';

@Component({
  selector: 'mkv-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'mkv-app';

  constructor(private authService: AuthService, private store: Store<fromAppState.AppState>, private jwtService: JWTService) { }

  ngOnInit() {

    // checken, ob das ein redirect vom authprovider war
    const hash = window.location.hash;
    if (hash && hash.indexOf('idToken') >= 0) {
      const authResult = this.jwtService.parseHash(hash);
      this.fetchSession(authResult);
    } else {

      // schauen, ob es noch eine session im localStorage gibt (das ist bei refresh der Fall
    }
  }

  private fetchSession(authResult: AuthResult) {

    this.authService.createSession(authResult).pipe(
      tap(session => {

        this.store.dispatch(AuthActions.createSession({ session }));

      })
    ).subscribe(
      noop,
      () => alert('login Failed')
    );
  }
}
