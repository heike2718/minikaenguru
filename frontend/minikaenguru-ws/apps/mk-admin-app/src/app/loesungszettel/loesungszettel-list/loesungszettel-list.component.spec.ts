import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoesungszettelListComponent } from './loesungszettel-list.component';

describe('LoesungszettelListComponent', () => {
  let component: LoesungszettelListComponent;
  let fixture: ComponentFixture<LoesungszettelListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LoesungszettelListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoesungszettelListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
