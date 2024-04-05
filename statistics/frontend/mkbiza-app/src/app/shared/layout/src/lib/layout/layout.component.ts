import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { tap } from 'rxjs';

@Component({
  selector: 'mkbiza-layout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent implements OnInit {

  #isHandset = false;

  #breakpointObserver = inject(BreakpointObserver);


  get isHandset(): boolean {
    return this.#isHandset;
  }

  ngOnInit(): void {
    this.#breakpointObserver.observe(Breakpoints.Handset).pipe(
      tap((state: BreakpointState) => {
        if (state.matches) {
          this.#isHandset = true;
        } else {
          this.#isHandset = false;
        }
      })
    ).subscribe();
  }
}
