import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem, MessageService } from 'primeng/api';

import { enumToString, sleep } from 'src/app/shared/shared.module';
import { OrdersService } from '../../orders.service';
import { firstValueFrom } from 'rxjs';
import { OrderItemModel, OrderModel, OrderStatus, PaymentMethodType } from '../../models/model';
import { InvoicesService } from '../../../payments/invoices.service';
import { InvoiceModel, PaymentMethod } from '../../../payments/models/model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-order-detail-page',
  templateUrl: './order-detail-page.component.html',
  styleUrl: './order-detail-page.component.css'
})
export class OrderDetailPageComponent implements OnInit {
  order?: OrderModel;
  orderItems?: OrderItemModel[] = [];
  invoice?: InvoiceModel;
  orderCode?: string;
  loading = true;
  loadingInvoice = true;
  invoiceNotFound = false;
  menuItems: MenuItem[] = [];

  canShowShippingInfo: boolean = false;
  canShowBillingInfo: boolean = false;

  constructor(
    private ordersService: OrdersService,
    private invoicesService: InvoicesService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    ) { }

  ngOnInit(): void {
    this.orderCode = this.route.snapshot.params['orderCode'];
    this.loadData();
    this.setupMenus();
  }

  private loadData() {
    this.loading = true; // on page refresh
    sleep()
      .then(()=> {
        this.loadOrder();
      });
  }

  private setupMenus() {
    this.menuItems = this.menuItems = [
      {
        label: 'Options',
        items: [
          {
            label: 'Edit',
            command: () => {
              this.edit();
            }
          }
        ]
      }
    ];
  }

  private edit() {
    if (this.orderCode) {
      this.router.navigate([`/orders/${this.orderCode}/edit`])
    }
  }

  private loadOrder() {
    firstValueFrom(this.ordersService.getOne(this.orderCode!))
      .then(order => {
        this.order = order;
        this.orderItems = order.items || [];
        this.loading = false;
        this.loadInvoice(order.id);
      })
      .catch(e => {
        console.error(e);
        let message = {
            severity:'error',
            summary: 'Server connection error',
            detail: 'Unable to retrieve order',
            boolean: true
        }
        this.messageService.add(message);
      });
  }

  private loadInvoice(orderId: string) {
    this.loadingInvoice = true;
    this.invoiceNotFound = false;

    firstValueFrom(this.invoicesService.getByOrderId(orderId))
      .then(invoice => {
        this.invoice = invoice;
        this.loadingInvoice = false;
      })
      .catch((e: HttpErrorResponse) => {
        this.loadingInvoice = false;
        if (e.status === 404) {
          this.invoiceNotFound = true;
          return;
        }
        this.messageService.add({
          severity:'error',
          summary: 'Server connection error',
          detail: 'Unable to retrieve invoice'
        });
      });
  }

  showShippingInfo(){
    this.canShowShippingInfo = true;
  }
  showBillingInfo(){
    this.canShowBillingInfo = true;
  }

  public enumStatusToValue(enumObj: OrderStatus) {
    return enumToString(OrderStatus, enumObj);
  }
  public enumPaymentMethodToValue(enumObj: PaymentMethodType) {
    return enumToString(PaymentMethodType, enumObj);
  }

  public enumInvoicePaymentMethodToValue(enumObj: PaymentMethod | undefined) {
    return enumToString(PaymentMethod, enumObj);
  }

}
