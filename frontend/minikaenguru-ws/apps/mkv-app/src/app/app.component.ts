import { Component } from '@angular/core';
import { environment } from '../environments/environment';

@Component({
  selector: 'mkv-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Minikänguru Onlineauswertung';

  envName = environment.envName;
  showEnv = !environment.production;
  api = environment.apiUrl;
  version = environment.version;

}
