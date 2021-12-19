import { WettbewerbStatus } from '@minikaenguru-ws/common-components'; 

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
};

export interface WettbewerbWithID {
	jahr: number;
	wettbewerb: Wettbewerb;
}

// verpackt häufig erforderliche Operationen auf einem WettbewerbWithID[] etwas handhabbarer.
export class WettbewerbeMap {

	private wettbewerbe: Map<number, Wettbewerb> = new Map();

	constructor(readonly items: WettbewerbWithID[]) {
		if (items !== undefined) {
			for (const wb of items) {
				this.wettbewerbe.set(wb.jahr, wb.wettbewerb);
			}
		}
	}

	public has(jahr: number): boolean {

		if (jahr === NaN) {
			return false;
		}
		return this.wettbewerbe.has(jahr);
	}

	public get(jahr: number): Wettbewerb | undefined {

		if (!this.has(jahr)) {
			return undefined;
		}

		return this.wettbewerbe.get(jahr);
	}

	public toArray(): Wettbewerb[] {

		return [...this.wettbewerbe.values()];
	}

	public merge(wettbewerb: Wettbewerb): WettbewerbWithID[] {

		const result: WettbewerbWithID[] = [];
		for (const item of this.items) {
			if (item.jahr !== wettbewerb.jahr) {
				result.push(item);
			} else {
				result.push({ jahr: wettbewerb.jahr, wettbewerb: wettbewerb });
			}
		}
		return result;
	}
}
