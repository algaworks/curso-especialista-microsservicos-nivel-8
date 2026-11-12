import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryDetailPageComponent } from './category-detail-page.component';

describe('CategoryDetailPageComponent', () => {
  let component: CategoryDetailPageComponent;
  let fixture: ComponentFixture<CategoryDetailPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CategoryDetailPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CategoryDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
