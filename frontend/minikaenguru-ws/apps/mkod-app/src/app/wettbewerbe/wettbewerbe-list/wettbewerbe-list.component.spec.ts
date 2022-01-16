import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WettbewerbeListComponent } from './wettbewerbe-list.component';

describe('WettbewerbeListComponent', () => {
  let component: WettbewerbeListComponent;
  let fixture: ComponentFixture<WettbewerbeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WettbewerbeListComponent],
      teardown: { destroyAfterEach: false },
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WettbewerbeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
