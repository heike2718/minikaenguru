import { Privatteilnahme, AnonymisierteTeilnahme } from '../wettbewerb/wettbewerb.model';


export interface Privatveranstalter {
	readonly hatZugangZuUnterlangen: boolean;
	readonly aktuelleTeilnahme?: Privatteilnahme;
	readonly anonymisierteTeilnahmen: AnonymisierteTeilnahme[];
}
