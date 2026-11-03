import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerCreatePageComponent } from './customer-create-page.component';

describe('CustomerCreatePageComponent', () => {
  let component: CustomerCreatePageComponent;
  let fixture: ComponentFixture<CustomerCreatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CustomerCreatePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerCreatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
