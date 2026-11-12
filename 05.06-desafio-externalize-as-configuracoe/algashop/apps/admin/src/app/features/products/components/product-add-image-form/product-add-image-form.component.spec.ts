import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductAddImageFormComponent } from './product-add-image-form.component';

describe('ProductAddImageFormComponent', () => {
  let component: ProductAddImageFormComponent;
  let fixture: ComponentFixture<ProductAddImageFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProductAddImageFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProductAddImageFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
