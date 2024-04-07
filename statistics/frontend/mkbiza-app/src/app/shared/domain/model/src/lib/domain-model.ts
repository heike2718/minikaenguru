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

