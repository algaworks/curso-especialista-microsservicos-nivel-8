import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { OrderFilter, OrderModel, OrderStatus } from './../../models/model';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { enumToOptions, enumToString } from 'src/app/shared/shared.module';
import { AutoCompleteCompleteEvent } from 'primeng/autocomplete';


type OrderInputControls = { [key in keyof OrderModel]: AbstractControl}
type OrderInputFormGroup = FormGroup & {value: OrderModel, control: OrderInputControls}

@Component({
  selector: 'app-order-filter',
  templateUrl: './order-filter.component.html',
  styleUrl: './order-filter.component.css'
})
export class OrderFilterComponent implements OnInit {

  form?: OrderInputFormGroup;
  statusList: any[] = [];
  filteredStatusList: any[] = [];
  searching = false;

  @Output() doFilter: EventEmitter<OrderFilter> = new EventEmitter<OrderFilter>();
  @Input() appliedFilter = new OrderFilter();

  constructor(
  ) { }

  ngOnInit(): void {
    this.configureForm();
    this.loadData();
    this.loadFilterState();
  }

  private configureForm() {
    this.form = new FormGroup({
      status: new FormControl(null),
      placedAtFrom: new FormControl(null),
      placedAtTo: new FormControl(null),
      totalAmountFrom: new FormControl(null),
      totalAmountTo: new FormControl(null),
      code: new FormControl(null),
    }) as OrderInputFormGroup;
  }

  private loadData() {
    this.statusList = enumToOptions(OrderStatus);
  }

  loadFilterState() {
    if(this.appliedFilter.orderCode){
      this.form!.controls.code.setValue(this.appliedFilter.orderCode);
    }
    if(this.appliedFilter.status){
      let status = {label: enumToString(OrderStatus, this.appliedFilter.status), value: this.appliedFilter.status}
      this.form!.controls.status.setValue(status);
    }
    if(this.appliedFilter.placedAtFrom){
      this.form!.controls.placedAtFrom.setValue(new Date(this.appliedFilter.placedAtFrom));
    }
    if(this.appliedFilter.placedAtTo){
      this.form!.controls.placedAtTo.setValue(new Date(this.appliedFilter.placedAtTo));
    }
    if(this.appliedFilter.totalAmountFrom){
      this.form!.controls.totalAmountFrom.setValue(this.appliedFilter.totalAmountFrom);
    }
    if(this.appliedFilter.totalAmountTo){
      this.form!.controls.totalAmountTo.setValue(this.appliedFilter.totalAmountTo);
    }
  }

  public search(){
    const orderFilter = this.generateFilter();
    this.doFilter.emit(orderFilter);
  }

  private generateFilter() : OrderFilter {

    const orderFilter: OrderFilter = new OrderFilter();
    if(this.form!.controls.status.value){
      orderFilter.status = this.form!.controls.status.value.value || this.form!.controls.status.value;
    }
    if(this.form!.controls.placedAtFrom.value){
      orderFilter.placedAtFrom = this.form!.controls.placedAtFrom.value
    }
    if(this.form!.controls.placedAtTo.value){
      orderFilter.placedAtTo = this.form!.controls.placedAtTo.value
    }
    if(this.form!.controls.totalAmountFrom.value){
      orderFilter.totalAmountFrom = this.form!.controls.totalAmountFrom.value
    }
    if(this.form!.controls.totalAmountTo.value){
      orderFilter.totalAmountTo = this.form!.controls.totalAmountTo.value
    }
    if(this.form!.controls.code.value){
      orderFilter.orderCode = this.form!.controls.code.value
    }
    if(this.appliedFilter.customerId){
      orderFilter.customerId = this.appliedFilter.customerId
    }

    return orderFilter;
  }


  public filterStatus(event: AutoCompleteCompleteEvent){
    let filtered: any[] = [];
    let query = event.query;

    for (let i = 0; i < (this.statusList as any[]).length; i++) {
        let status = (this.statusList as any[])[i];
        if (status.label.toLowerCase().indexOf(query.toLowerCase()) == 0) {
            filtered.push(status);
        }
    }

    this.filteredStatusList = filtered;
  }
  public clearFilter(){
    this.form!.reset();
    this.search();
  }

}
