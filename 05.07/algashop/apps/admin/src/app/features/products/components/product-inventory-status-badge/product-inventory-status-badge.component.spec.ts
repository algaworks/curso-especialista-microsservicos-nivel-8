import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductInventoryStatusBadgeComponent } from './product-inventory-status-badge.component';

describe('ProductInventoryStatusBadgeComponent', () => {
  let component: ProductInventoryStatusBadgeComponent;
  let fixture: ComponentFixture<ProductInventoryStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProductInventoryStatusBadgeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProductInventoryStatusBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
