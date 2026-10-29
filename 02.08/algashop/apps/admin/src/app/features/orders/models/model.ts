import { HttpParams } from "@angular/common/http";
import { applySortParams } from "src/app/core/models";

export interface OrderModel {
  id: string;
  customer: CustomerMinimalModel;
  totalItems: number;
  totalAmount: number;
  status: OrderStatus;
  placedAt: string;
  canceledAt?: string;
  paidAt?: string;
  readyAt?: string;
  paymentMethod: PaymentMethodType;
  creditCardId?: string;
  shipping?: ShippingModel;
  billing?: BillingModel;
  items?: OrderItemModel[];
}
export interface OrderItemModel {
  id: string;
  productId: string
  productName: string
  price: number
  quantity: number
  totalAmount: number
}

export class OrderFilter {
  order?: string;
  direction?: string;
  page: number = 0;
  size: number = 10;
  sort: string = `${OrderSortProperty.PLACE_AT},ASC`;

  status?: any;
  placedAtFrom?: Date;
  placedAtTo?: Date;
  totalAmountFrom?: number;
  totalAmountTo?: number;
  orderCode?: string;
  customerId?: string;

  constructor(data?: {}) {
    // this.applyParams(data);
  }

  applyParams(params?: any) {
      Object.assign(this, params);

      if(params?.status){
        this.status = params.status;
      }
  }

  applyPage(params?: any) {
      if (!params) {
          return;
      }

      if (params.direction) {
          this.direction = params.direction
      }

      if (params.size) {
          this.size = params.size
      }

      if (params.order) {
          this.order = params.order
      }

      if (params.page) {
          this.page = params.page
      }
  }

  toQueryParams() : HttpParams {
      let params = new HttpParams()
          .set('page', this.page)
          .set('size', this.size);

      if(this.status){
        params = params.set('status', this.status.value || this.status);
      }
      if(this.placedAtFrom){
        const formattedDate = new Date(this.placedAtFrom).toISOString().substring(0, 10);
        params = params.set('placedAtFrom', formattedDate);
      }
      if(this.placedAtTo){
        const formattedDate = new Date(this.placedAtTo).toISOString().substring(0, 10);
        params = params.set('placedAtTo', formattedDate);
      }
      if(this.totalAmountFrom){
        params = params.set('totalAmountFrom', this.totalAmountFrom);
      }
      if(this.totalAmountTo){
        params = params.set('totalAmountTo', this.totalAmountTo);
      }
      if(this.orderCode){
        params = params.set('orderId', this.orderCode);
      }
      if(this.customerId){
        params = params.set('customerId', this.customerId);
      }

      return applySortParams(params, this.sort);
  }
}

export enum OrderSortProperty {
  PLACE_AT = 'PLACE_AT',
  PAID_AT = 'PAID_AT',
  CANCELED_AT = 'CANCELED_AT',
  STATUS = 'STATUS',
}

export enum OrderStatus {
  DRAFT = "Draft",
  PLACED = "Placed",
  PAID = "Paid",
  READY = "Ready",
  CANCELED = "Canceled",
}
export enum PaymentMethodType {
  CREDIT_CARD = "Credit card",
  GATEWAY_BALANCE = "Gateway Balance"
}

export interface CustomerModel {
  id: string;
  firstName: string;
  lastName: string;
}

export interface CustomerMinimalModel {
  id: string;
  firstName: string;
  lastName: string;
  document: string;
  email: string;
  phone: string;
}

export interface RecipientModel {
  firstName: string
  lastName: string
  document: string
  phone: string
}

export interface BillingModel extends RecipientModel {
  email: string
  address: AddressModel
}

export interface ShippingModel {
  cost: number
  expectedDate: string
  recipient: RecipientModel
  address: AddressModel
}

export interface AddressModel {
  street: string;
  number: string;
  complement: string;
  neighborhood: string;
  city: string;
  state: string;
  zipCode: string;
}
