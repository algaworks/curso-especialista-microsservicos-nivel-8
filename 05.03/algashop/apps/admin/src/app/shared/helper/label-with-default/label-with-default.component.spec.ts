import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LabelWithDefaultComponent } from './label-with-default.component';

describe('LabelWithDefaultComponent', () => {
  let component: LabelWithDefaultComponent;
  let fixture: ComponentFixture<LabelWithDefaultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LabelWithDefaultComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LabelWithDefaultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
