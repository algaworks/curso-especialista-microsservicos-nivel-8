import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { CustomerFilter, CustomerModel } from '../../models/model';


type CustomerInputControls = { [key in keyof CustomerModel]: AbstractControl}
type CustomerInputFormGroup = FormGroup & {value: CustomerModel, control: CustomerInputControls}
@Component({
  selector: 'app-customer-filter',
  templateUrl: './customer-filter.component.html',
  styleUrl: './customer-filter.component.css'
})
export class CustomerFilterComponent implements OnInit{

  form?: CustomerInputFormGroup;
  searching = false;

  @Output() doFilter: EventEmitter<CustomerFilter> = new EventEmitter<CustomerFilter>();
  @Input() appliedFilter = new CustomerFilter();

  ngOnInit(): void {
    this.configureForm();
    this.loadFilterState();
  }

  private configureForm() {
    this.form = new FormGroup({
      firstName: new FormControl(''),
      email: new FormControl(''),
    }) as CustomerInputFormGroup;
  }

  private loadFilterState() {
    if(this.appliedFilter.firstName){
      this.form!.controls.firstName.setValue(this.appliedFilter.firstName);
    }
    if(this.appliedFilter.email){
      this.form!.controls.email.setValue(this.appliedFilter.email);
    }
  }

  public search(){
    const customerFilter = this.generateFilter();
    this.doFilter.emit(customerFilter);
  }

  private generateFilter() : CustomerFilter {

    const customerFilter: CustomerFilter = new CustomerFilter();
    if(this.form!.controls.firstName.value){
      customerFilter.firstName = this.form!.controls.firstName.value;
    }
    if(this.form!.controls.email.value){
      customerFilter.email = this.form!.controls.email.value;
    }

    return customerFilter;
  }


  public clearFilter(){
    this.form!.reset();
    this.search();
  }


}
