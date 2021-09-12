import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeilnahmenCardComponent } from './teilnahmen-card.component';

describe('TeilnahmenCardComponent', () => {
  let component: TeilnahmenCardComponent;
  let fixture: ComponentFixture<TeilnahmenCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TeilnahmenCardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TeilnahmenCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
