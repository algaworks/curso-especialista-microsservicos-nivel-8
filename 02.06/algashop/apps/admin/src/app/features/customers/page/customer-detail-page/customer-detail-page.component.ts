import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';

import { sleep } from 'src/app/shared/shared.module';
import { CustomersService } from '../../customers.service';
import { firstValueFrom } from 'rxjs';
import { CustomerModel } from '../../models/model';

@Component({
  selector: 'app-customer-detail-page',
  templateUrl: './customer-detail-page.component.html',
  styleUrl: './customer-detail-page.component.css'
})
export class CustomerDetailPageComponent implements OnInit {
  customer?: CustomerModel;
  customerId?: string;
  loading = true;

  constructor(
    private customersService: CustomersService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    ) { }

  ngOnInit(): void {
    this.customerId = this.route.snapshot.params['customerId'];
    this.loadData();
  }

  private loadData() {
    this.loading = true; // on page refresh
    sleep()
      .then(()=> {
        this.loadCustomer();
      });
  }

  private loadCustomer() {
    firstValueFrom(this.customersService.getOne(this.customerId!))
      .then(customer => {
        this.customer = customer;
        this.loading = false;
      })
      .catch(e => {
        console.error(e);
        let message = {
            severity:'error',
            summary: 'Connection error with the server',
            detail: 'Unable to retrieve the customer',
            boolean: true
        }
        this.messageService.add(message);
      });
  }

}
