import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { TeilnahmenFacade } from '../../teilnahmen/teilnahmen.facade';
