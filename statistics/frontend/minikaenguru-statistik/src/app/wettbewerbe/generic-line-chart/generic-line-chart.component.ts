import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { CommonModule } from "@angular/common";
import { booleanAttribute, Component, inject, Input, OnInit, ViewChild } from "@angular/core";
import { ChartConfiguration, ChartData, ChartEvent, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';


@Component({
    selector: 'mks-line-chart',
    imports: [CommonModule, BaseChartDirective],
    templateUrl: './generic-line-chart.component.html',
    styleUrl: './generic-line-chart.component.scss'
})
export class GenericLineChartComponent implements OnInit {

    @ViewChild(BaseChartDirective) chart: BaseChartDirective<'bar'> | undefined;

    @Input()
    headline = '';

    @Input()
    chartData!: ChartData<'line'>;

    chartOptions!: ChartConfiguration<'line'>['options'];

    @Input()
    myCanvasId: string = '';

    @Input({ transform: booleanAttribute })
    showLegend!: boolean;

    @Input()
    breite = '90vw;'

    #breakpointObserver = inject(BreakpointObserver);

    ngOnInit(): void {

        this.chartOptions = {
            maintainAspectRatio: false,
            responsive: true,
            scales: {
                x: {},
                y: {
                    min: 0,
                    position: 'left'
                },
            },
            plugins: {
                legend: {
                    display: this.showLegend,
                }
            },
            elements: {
                line: {
                    tension: 0
                }
            }
        }
    }

    get isHandset(): boolean {
        return this.#breakpointObserver.isMatched(Breakpoints.Handset);
    }

    getStyle(): string {
        return 'position: relative; height: 30vh; width: ' + this.breite;
    }
}