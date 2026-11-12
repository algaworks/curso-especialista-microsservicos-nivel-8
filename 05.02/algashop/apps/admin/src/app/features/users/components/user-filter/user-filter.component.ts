import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { UserFilter, UserModel, UserType } from '../../models/model';
import { enumToOptions, enumToString } from 'src/app/shared/shared.module';
import { AutoCompleteCompleteEvent } from 'primeng/autocomplete';


type UserInputControls = { [key in keyof UserModel]: AbstractControl}
type UserInputFormGroup = FormGroup & {value: UserModel, control: UserInputControls}
@Component({
  selector: 'app-user-filter',
  templateUrl: './user-filter.component.html',
  styleUrl: './user-filter.component.css'
})
export class UserFilterComponent implements OnInit{

  form?: UserInputFormGroup;
  typeList: any[] = [];
  filteredTypeList: any[] = [];
  searching = false;

  @Output() doFilter: EventEmitter<UserFilter> = new EventEmitter<UserFilter>();
  @Input() appliedFilter = new UserFilter();

  ngOnInit(): void {
    this.configureForm();
    this.loadData();
    this.loadFilterState();
  }

  private configureForm() {
    this.form = new FormGroup({
      email: new FormControl(''),
      type: new FormControl(null)
    }) as UserInputFormGroup;
  }

  private loadData() {
    this.typeList = enumToOptions(UserType);
  }

  private loadFilterState() {
    if(this.appliedFilter.email){
      this.form!.controls.email.setValue(this.appliedFilter.email);
    }
    if(this.appliedFilter.type){
      let type = {label: enumToString(UserType, this.appliedFilter.type), value: this.appliedFilter.type}
      this.form!.controls.type.setValue(type);
    }
  }

  public search(){
    const userFilter = this.generateFilter();
    this.doFilter.emit(userFilter);
  }

  private generateFilter() : UserFilter {

    const userFilter: UserFilter = new UserFilter();
    if(this.form!.controls.email.value){
      userFilter.email = this.form!.controls.email.value;
    }
    if(this.form!.controls.type.value){
      userFilter.type = this.form!.controls.type.value.value || this.form!.controls.type.value;
    }

    return userFilter;
  }

  public filterType(event: AutoCompleteCompleteEvent){
    let filtered: any[] = [];
    let query = event.query;

    for (let i = 0; i < (this.typeList as any[]).length; i++) {
        let type = (this.typeList as any[])[i];
        if (type.label.toLowerCase().indexOf(query.toLowerCase()) == 0) {
            filtered.push(type);
        }
    }

    this.filteredTypeList = filtered;
  }

  public clearFilter(){
    this.form!.reset();
    this.search();
  }


}
