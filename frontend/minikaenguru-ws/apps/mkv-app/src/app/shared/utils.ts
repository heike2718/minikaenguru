import { RouterReducerState,RouterStateSerializer } from '@ngrx/router-store';
import { RouterStateSnapshot, Params } from '@angular/router';
import { NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';

export const modalOptions: NgbModalOptions = {
  backdrop:'static',
  centered:true,
  ariaLabelledBy: 'modal-basic-title'
};

export interface RouterStateUrl {
  url: string;
  params: Params;
  queryParams: Params;
};

export interface State {
  router: RouterReducerState<RouterStateUrl>;
};

export class CustomRouterStateSerializer implements RouterStateSerializer<RouterStateUrl> {
  serialize(routerState: RouterStateSnapshot): RouterStateUrl {

	let route = routerState.root;

    while (route.firstChild) {
      route = route.firstChild;
    }

    const { url, root: { queryParams } } = routerState;
	const { params } = route;

    return { url, params, queryParams };
  }
};
