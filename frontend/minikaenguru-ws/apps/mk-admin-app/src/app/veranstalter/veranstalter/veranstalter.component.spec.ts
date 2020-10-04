import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VeranstalterComponent } from './veranstalter.component';

describe('VeranstalterComponent', () => {
  let component: VeranstalterComponent;
  let fixture: ComponentFixture<VeranstalterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VeranstalterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VeranstalterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
