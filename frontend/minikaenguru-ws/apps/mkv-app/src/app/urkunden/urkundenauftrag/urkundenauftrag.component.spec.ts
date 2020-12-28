import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UrkundenauftragComponent } from './urkundenauftrag.component';

describe('UrkundenauftragComponent', () => {
  let component: UrkundenauftragComponent;
  let fixture: ComponentFixture<UrkundenauftragComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UrkundenauftragComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UrkundenauftragComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
