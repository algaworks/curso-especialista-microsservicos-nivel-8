import { Component,  Input, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { CustomersService } from '../../customers.service';
import { firstValueFrom } from 'rxjs';
import { CustomerInput } from '../../models/model';

type CustomerInputControls = { [key in keyof CustomerInput]: AbstractControl}
type CustomerInputFormGroup = FormGroup & {value: CustomerInput, control: CustomerInputControls}

@Component({
  selector: 'app-customer-form',
  templateUrl: './customer-form.component.html',
  styleUrl: './customer-form.component.css'
})
export class CustomerFormComponent implements OnInit {
  form?: CustomerInputFormGroup;
  @Input() customerId?: string;
  saving = false;
  customerTypeOptions: any[] = [];
  maxBornDate: Date = new Date();

  constructor(
    private customersService: CustomersService,
    private messageService: MessageService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.configureForm();
  }

  save() {
    this.saving = true;

    let input = this.generateInput();

    firstValueFrom(this.customersService.create(input))
      .then((customer) => {
        this.saving = false;
        this.displaySuccessMessage();
        this.router.navigate([`/customers/${customer.id}`]);
      }).catch((e) => {
        this.saving = false;
        this.displayErrorMessage(e.error);
      })
  }

  isFormInvalid() {
    return this.form!.invalid;
  }


  private configureForm() {

    this.form = new FormGroup({
      fullName: new FormControl(null, [Validators.required]),
      email: new FormControl(null, [Validators.required, Validators.email]),
      phone: new FormControl(null, [Validators.required]),
      bornOn: new FormControl(null, [Validators.required]),
      document: new FormControl(null, [Validators.required]),
      allowPromotionNotifications: new FormControl(false, [Validators.required]),
      address: new FormGroup(
        {
          street: new FormControl(null, [Validators.required]),
          number: new FormControl(null, [Validators.required]),
          complement: new FormControl(null, [Validators.required]),
          neighborhood: new FormControl(null, [Validators.required]),
          city: new FormControl(null, [Validators.required]),
          state: new FormControl(null, [Validators.required]),
          zipCode: new FormControl(null, [Validators.required])
        }
      )
    }) as CustomerInputFormGroup;

  }

  private generateInput() : CustomerInput {
    return {...this.form?.value} as CustomerInput;

  }

  private displaySuccessMessage() {
    let message = {
      severity:'success',
      summary: 'Sucess',
      detail: 'Customer registered',
      boolean: true
    }
    this.messageService.add(message);
  }

  private displayErrorMessage(e: Error) {
    let message = {
      severity:'error',
      summary: 'Server connection error, please try again',
      detail: e.message,
      boolean: true
    }
    this.messageService.add(message);
  }
}
