import { Validator, NG_VALIDATORS, FormControl } from '@angular/forms'
import { Directive, OnInit, forwardRef, OnDestroy } from '@angular/core';
import { KlassenFacade } from '../klassen.facade';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { KlassenMap } from '../klassen.model';

@Directive({
	selector: '[mkvKlasseNameValidator]',
	providers: [
		{ provide: NG_VALIDATORS, useExisting: KlasseNameValidatorDirective, multi: true }
	]
})
export class KlasseNameValidatorDirective implements OnInit, OnDestroy, Validator {

	private klassenMapSubscription: Subscription = new Subscription();

	private routeSubscription: Subscription = new Subscription();

	private uuid?: string;

	private klassenMap!: KlassenMap;

	constructor(private route: ActivatedRoute,
		private klassenFacade: KlassenFacade) { }

	ngOnInit(): void {

		this.routeSubscription = this.route.paramMap.subscribe(

			paramMap => {
				const param = paramMap.get('id');
				if (param) {
					this.uuid = param;
				}
			}

		);

		this.klassenMapSubscription = this.klassenFacade.klassenMap$.subscribe(
			m => {
				this.klassenMap = new KlassenMap(m);
			}
		);

	}

	ngOnDestroy(): void {

		this.routeSubscription.unsubscribe();
		this.klassenMapSubscription.unsubscribe();

	}

	validate(c: FormControl) {

		const name: string = c.value;

		if (!name || name.length === 0) {
			return null;
		}

		if (this.klassenMap && this.klassenMap.containsName({ uuid: this.uuid, name: name })) {
			return {'klasseExists': true, 'rule': 'Es gibt bereits einen Klasse mit diesem Namen.'}
		}

		return null;
	}

}
