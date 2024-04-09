import { ChartData } from "chart.js";

export type StatusWettbewerb = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_LEHRER' | 'DOWNLOAD_PRIVAT' | 'BEENDET';
export type Klassenstufe = 'IKID' | 'EINS' | 'ZWEI';

export const BAR_BACKGROUND_COLOR_BLUE = 'rgba(54, 162, 235, 0.5)';
export const BAR_BACKGROUND_COLOR_YELLOW = 'rgba(255, 229, 169, 1)';
export const BAR_BACKGROUND_COLOR_GREENLY = 'rgba(165, 223, 222, 1)';
export interface Images {
  readonly imageFrage: string,
  readonly imageLoesung: string
};

export interface Gruppierungsitem {
  readonly name: string,
  readonly anzahl: number
};

export interface WettbewerbOverview {
  readonly jahr: number,
  readonly status: StatusWettbewerb,
  readonly anzahlKinder: number,
  readonly medianeJeKlassenstufe: Gruppierungsitem[]
  readonly kinderJeKlassenstufe: Gruppierungsitem[]
};

export interface StatistikJahreChartData {
  readonly chartDataJahreAnzahlKinder: ChartData<'bar'> | undefined;
  readonly chartDataJahreKinderKlassenstufe: ChartData<'bar'> | undefined;
  readonly chartDataJahreMediane: ChartData<'bar'> | undefined;
}

export interface WettbewerbDetails {
  readonly jahr: number;
  readonly beendet: boolean;
  readonly anzahlKinderGesamt: number;
  readonly anzahlPrivatanmeldungen: number;
  readonly anzahlSchulanmeldungen: number;
  readonly teilnehmendeSchulenGesamt: number;
  readonly klassenstufen: Klassenstufe[];
  readonly schulenJeLand: Gruppierungsitem;
  readonly schulkinderJeLand: Gruppierungsitem;
  readonly kinderJeTeilnahmeart: Gruppierungsitem;
  readonly kinderJeKlassenstufe: Gruppierungsitem;
  readonly kinderJeSprache: Gruppierungsitem;
  readonly medianeJeKlassenstufe: Gruppierungsitem;
};

export interface StatistikWettbewerbChartData {
  readonly chartDataSchulenJeLand: ChartData<'bar'> | undefined;
  readonly chartDataKinderJeLand: ChartData<'bar'> | undefined;
  readonly chartDataKinderJeTeilnahmeart: ChartData<'pie'> | undefined;
  readonly chartDataKinderJeKlassenstufe: ChartData<'pie'> | undefined;
  readonly chartDataKinderJeSprache: ChartData<'pie'> | undefined;
  readonly chartDataMedianMaxPunktzahlIKID: ChartData<'pie'> | undefined;
  readonly chartDataMedianMaxPunktzahlEINS: ChartData<'pie'> | undefined;
  readonly chartDataMedianMaxPunktzahlZWEI: ChartData<'pie'> | undefined;
};

export interface WettbewerbDetailsGUIModel {
  readonly wettbewerb: WettbewerbDetails;
  readonly chartData: StatistikWettbewerbChartData;
};
