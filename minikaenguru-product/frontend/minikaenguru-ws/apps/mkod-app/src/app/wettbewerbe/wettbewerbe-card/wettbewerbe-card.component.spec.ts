import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WettbewerbeCardComponent } from './wettbewerbe-card.component';

describe('WettbewerbeCardComponent', () => {
  let component: WettbewerbeCardComponent;
  let fixture: ComponentFixture<WettbewerbeCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WettbewerbeCardComponent],
      teardown: { destroyAfterEach: false },
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WettbewerbeCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
