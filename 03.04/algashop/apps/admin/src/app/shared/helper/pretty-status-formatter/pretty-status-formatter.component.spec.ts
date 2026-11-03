import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrettyStatusFormatterComponent } from './pretty-status-formatter.component';

describe('PrettyStatusFormatterComponent', () => {
  let component: PrettyStatusFormatterComponent;
  let fixture: ComponentFixture<PrettyStatusFormatterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrettyStatusFormatterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrettyStatusFormatterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
