import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WettbewerbDetailsComponent } from './wettbewerb-details.component';

describe('WettbewerbDetailsComponent', () => {
  let component: WettbewerbDetailsComponent;
  let fixture: ComponentFixture<WettbewerbDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WettbewerbDetailsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WettbewerbDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
