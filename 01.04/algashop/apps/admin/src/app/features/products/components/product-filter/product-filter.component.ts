import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { CategoryModel, ProductFilter, ProductInput } from 'src/app/core/models';
import { ProductsService } from '../../products.service';


type ProductInputControls = { [key in keyof ProductInput]: AbstractControl}
type ProductInputFormGroup = FormGroup & {value: ProductInput, control: ProductInputControls}

@Component({
  selector: 'app-product-filter',
  templateUrl: './product-filter.component.html',
  styleUrl: './product-filter.component.css'
})
export class ProductFilterComponent implements OnInit {
  form?: ProductInputFormGroup;
  searching = false;
  categories: CategoryModel[] = [];
  valorMinimo: number = 0;
  valorMaximo: number = 0;

  @Output() doFilter: EventEmitter<ProductFilter> = new EventEmitter<ProductFilter>();
  @Input() appliedFilter = new ProductFilter();

  constructor(
    private productsService: ProductsService,
    private cdr: ChangeDetectorRef
  ) { }

  async ngOnInit() {
    this.configureForm();
    await this.loadData();
    this.loadFilterState();
  }

  private configureForm() {
    this.form = new FormGroup({
      term: new FormControl(null),
      categories: new FormControl(null),
      enabled: new FormControl(false),
      inventoryStatus: new FormControl(null),
      hasDiscount: new FormControl<boolean>(false),
      priceFrom: new FormControl(null),
      priceTo: new FormControl(null),
    }) as ProductInputFormGroup;
  }

  private loadData(): Promise<void> {
    return firstValueFrom(this.productsService.getCategories())
      .then((categoriesPage)=> {
        this.categories = categoriesPage.content;
      });
  }

  private loadFilterState() {
    if(this.appliedFilter.term){
      this.form!.controls.term.setValue(this.appliedFilter.term);
    }

    if(this.appliedFilter.categoriesId){

      if(typeof this.appliedFilter.categoriesId === "string"){
        this.appliedFilter.categoriesId = [this.appliedFilter.categoriesId];
      }

      let selectedCategories = this.categories.filter(category =>
        this.appliedFilter?.categoriesId?.includes(category.id)
      )
      this.form!.controls.categories.setValue(selectedCategories);
    }
    if(this.appliedFilter.enabled){
      this.form!.controls.enabled.setValue(this.stringToBoolean(this.appliedFilter.enabled));
    }
    if(this.appliedFilter.inventoryStatus){
      this.form!.controls.inventoryStatus.setValue(this.appliedFilter.inventoryStatus);
    }
    if(this.appliedFilter.hasDiscount) {
      this.form!.controls.hasDiscount.setValue(this.stringToBoolean(this.appliedFilter.hasDiscount));
    }
    if(this.appliedFilter.priceFrom){
      this.form!.controls.priceFrom.setValue(this.appliedFilter.priceFrom);
    }
    if(this.appliedFilter.priceTo){
      this.form!.controls.priceTo.setValue(this.appliedFilter.priceTo);
    }

  }

  public search(){
    const productFilter = this.generateFilter();
    this.doFilter.emit(productFilter);
  }

  private generateFilter() : ProductFilter {

    const productFilter: ProductFilter = new ProductFilter();
    if(this.form!.controls.term.value){
      productFilter.term = this.form!.controls.term.value;
    }
    if(this.form!.controls.enabled.value){
      productFilter.enabled = this.form!.controls.enabled.value
    }
    if(this.form!.controls.hasDiscount.value){
      productFilter.hasDiscount = this.form!.controls.hasDiscount.value
    }
    if(this.form!.controls.priceFrom.value){
      productFilter.priceFrom = this.form!.controls.priceFrom.value
    }
    if(this.form!.controls.priceTo.value){
      productFilter.priceTo = this.form!.controls.priceTo.value
    }

    if(this.form!.controls.categories.value){
      productFilter.categoriesId = [];
      this.form!.controls.categories.value.forEach((category: any) => {
        productFilter.categoriesId!.push(category.id)
      });
    }


    return productFilter;
  }

  private stringToBoolean(value: any): boolean {
    return String(value) === 'true';
  }

  public clearFilter() {
    this.form!.reset();
    this.search();
  }

}
