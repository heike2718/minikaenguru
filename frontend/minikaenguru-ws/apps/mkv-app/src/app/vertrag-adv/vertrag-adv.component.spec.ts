import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VertragAdvComponent } from './vertrag-adv.component';

describe('VertragAdvComponent', () => {
  let component: VertragAdvComponent;
  let fixture: ComponentFixture<VertragAdvComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VertragAdvComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VertragAdvComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
