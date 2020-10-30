import { TeilnahmeIdentifier, Klassenstufe, Sprache, Kind } from '@minikaenguru-ws/common-components';

export interface KindWithID {
	readonly uuid: string;
	readonly kind: Kind;
};

// verpackt häufig erforderliche Operationen auf einem KindWithID[] etwas handhabbarer.
export class KinderMap {

	private kinder: Map<string, Kind> = new Map();

	constructor(readonly items: KindWithID[]) {
		if (items !== undefined) {
			for (const k of items) {
				this.kinder.set(k.uuid, k.kind);
			}
		}
	}

	public has(uuid: string): boolean {

		return this.kinder.has(uuid);
	}

	public get(uuid: string): Kind {

		if (!this.has(uuid)) {
			return null;
		}

		return this.kinder.get(uuid);
	}

	public toArray(): Kind[] {

		const array = [...this.kinder.values()];
		array.sort((kind1, kind2) => kind1.vorname.localeCompare(kind2.vorname));
		return array;
	}

	public merge(kind: Kind): KindWithID[] {

		const result: KindWithID[] = [];

		if (!this.has(kind.uuid)) {
			result.push({ uuid: kind.uuid, kind: kind });
		}
		for (const item of this.items) {
			if (item.uuid !== kind.uuid) {
				result.push(item);
			} else {
				result.push({ uuid: kind.uuid, kind: kind });
			}
		}
		return result;
	}
}
