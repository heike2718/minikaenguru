import { ChartData } from 'chart.js';
import { BAR_BACKGROUND_COLOR_BLUE, BAR_BACKGROUND_COLOR_GREENLY, BAR_BACKGROUND_COLOR_YELLOW, WettbewerbOverview } from './domain-model';

export function mapToChartDataJahreAnzahlKinder(wettbewerbe: WettbewerbOverview[]): ChartData<'bar'> {

    /*
    barChartData: ChartData<'bar'> = {
    labels: ['2006', '2007', '2008', '2009', '2010', '2011', '2012'],
    datasets: [
      { data: [65, 59, 80, 81, 56, 55, 40], label: 'Series A' },
      { data: [28, 48, 40, 19, 86, 27, 90], label: 'Series B' },
    ],
  };
  */

    let wettbewerbeReversed: WettbewerbOverview[] = [...wettbewerbe].reverse();

    const labels: string[] = wettbewerbeReversed.map(w => w.jahr + '');
    const data: number[] = wettbewerbeReversed.map(w => w.anzahlKinder);

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [...data],
                label: 'Anzahl Kinder',
                backgroundColor: BAR_BACKGROUND_COLOR_BLUE,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;
}

export function mapToChartDataJahreMediane(wettbewerbe: WettbewerbOverview[]): ChartData<'bar'> {

    let wettbewerbeReversed: WettbewerbOverview[] = [...wettbewerbe].reverse();
    const labels: string[] = wettbewerbeReversed.map(w => w.jahr + '');

    const dataIKIDS = wettbewerbeReversed.map(w => w.medianeJeKlassenstufe[0] ? w.medianeJeKlassenstufe[0].anzahl / 1000 : 0);
    const dataEINS = wettbewerbeReversed.map(w => w.medianeJeKlassenstufe[1] ? w.medianeJeKlassenstufe[1].anzahl / 1000 : 0);
    const dataZWEI = wettbewerbeReversed.map(w => w.medianeJeKlassenstufe[2] ? w.medianeJeKlassenstufe[2].anzahl / 1000 : 0);

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [...dataIKIDS],
                label: 'Inklusion',
                backgroundColor: BAR_BACKGROUND_COLOR_BLUE,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            },
            {
                data: [...dataEINS],
                label: 'Klasse 1',
                backgroundColor: BAR_BACKGROUND_COLOR_YELLOW,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            },
            {
                data: [...dataZWEI],
                label: 'Klasse 2',
                backgroundColor: BAR_BACKGROUND_COLOR_GREENLY,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;
}

export function mapToChartDataJahreKinderKlassenstufe(wettbewerbe: WettbewerbOverview[]): ChartData<'bar'> {

    let wettbewerbeReversed: WettbewerbOverview[] = [...wettbewerbe].reverse();
    const labels: string[] = wettbewerbeReversed.map(w => w.jahr + '');

    const dataIKIDS = wettbewerbeReversed.map(w => w.kinderJeKlassenstufe[0] ? w.kinderJeKlassenstufe[0].anzahl : 0);
    const dataEINS = wettbewerbeReversed.map(w => w.kinderJeKlassenstufe[1] ? w.kinderJeKlassenstufe[1].anzahl : 0);
    const dataZWEI = wettbewerbeReversed.map(w => w.kinderJeKlassenstufe[2] ? w.kinderJeKlassenstufe[2].anzahl : 0);

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [...dataIKIDS],
                label: 'Inklusion',
                backgroundColor: BAR_BACKGROUND_COLOR_BLUE,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            },
            {
                data: [...dataEINS],
                label: 'Klasse 1',
                backgroundColor: BAR_BACKGROUND_COLOR_YELLOW,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            },
            {
                data: [...dataZWEI],
                label: 'Klasse 2',
                backgroundColor: BAR_BACKGROUND_COLOR_GREENLY,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;
}



function adjustBorderColors(backgroundColor: string, factor: number): string {

    const rgbValues = backgroundColor.split(',').map(str => parseInt(str.replace(/\D/g, '')));
    const darkerRgbValues = rgbValues.map(value => value * factor);

    console.log(backgroundColor + ' => ' + darkerRgbValues);

    return `rgba(${darkerRgbValues[0]},${darkerRgbValues[1]},${darkerRgbValues[2]},${darkerRgbValues[3]})`
}