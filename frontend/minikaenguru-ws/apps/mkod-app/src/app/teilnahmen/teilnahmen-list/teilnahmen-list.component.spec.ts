import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeilnahmenListComponent } from './teilnahmen-list.component';

describe('TeilnahmenListComponent', () => {
  let component: TeilnahmenListComponent;
  let fixture: ComponentFixture<TeilnahmenListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TeilnahmenListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TeilnahmenListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
