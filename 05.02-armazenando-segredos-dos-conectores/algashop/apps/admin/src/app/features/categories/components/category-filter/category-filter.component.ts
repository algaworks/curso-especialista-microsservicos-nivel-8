import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { CategoryFilter, CategoryModel } from '../../models/models';


type CategoryInputControls = { [key in keyof CategoryModel]: AbstractControl}
type CategoryInputFormGroup = FormGroup & {value: CategoryModel, control: CategoryInputControls}
@Component({
  selector: 'app-category-filter',
  templateUrl: './category-filter.component.html',
  styleUrl: './category-filter.component.css'
})
export class CategoryFilterComponent implements OnInit{

  form?: CategoryInputFormGroup;
  searching = false;

  @Output() doFilter: EventEmitter<CategoryFilter> = new EventEmitter<CategoryFilter>();
  @Input() appliedFilter = new CategoryFilter();

  ngOnInit(): void {
    this.configureForm();
    this.loadFilterState();
  }

  private configureForm() {
    this.form = new FormGroup({
      name: new FormControl(''),
      enabled: new FormControl(false)
    }) as CategoryInputFormGroup;
  }

  private loadFilterState() {
    if(this.appliedFilter.name){
      this.form!.controls.name.setValue(this.appliedFilter.name);
    }
    if(this.appliedFilter.enabled){
      this.form!.controls.enabled.setValue(this.stringToBoolean(this.appliedFilter.enabled));
    }
  }

  public search(){
    const categoryFilter = this.generateFilter();
    this.doFilter.emit(categoryFilter);
  }

  private generateFilter() : CategoryFilter {

    const categoryFilter: CategoryFilter = new CategoryFilter();
    if(this.form!.controls.name.value){
      categoryFilter.name = this.form!.controls.name.value;
    }
    if(this.form!.controls.enabled.value){
      categoryFilter.enabled = this.form!.controls.enabled.value;
    }

    return categoryFilter;
  }

  private stringToBoolean(value: any): boolean {
    return String(value) === 'true';
  }

  public clearFilter(){
    this.form!.reset();
    this.search();
  }


}
