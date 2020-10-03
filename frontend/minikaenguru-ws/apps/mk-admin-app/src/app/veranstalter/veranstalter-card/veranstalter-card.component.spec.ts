import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VeranstalterCardComponent } from './veranstalter-card.component';

describe('VeranstalterCardComponent', () => {
  let component: VeranstalterCardComponent;
  let fixture: ComponentFixture<VeranstalterCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VeranstalterCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VeranstalterCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
