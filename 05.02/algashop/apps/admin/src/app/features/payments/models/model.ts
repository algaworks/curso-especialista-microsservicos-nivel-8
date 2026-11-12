export interface InvoiceModel {
  id: string;
  orderId: string;
  customerId: string;
  issuedAt: string;
  paidAt?: string;
  canceledAt?: string;
  expiresAt: string;
  totalAmount: number;
  cancelReason?: string;
  status: InvoiceStatus;
  paymentSettings?: PaymentSettingsModel;
  payer: PayerModel;
  items: LineItemModel[];
}

export interface LineItemModel {
  number: number;
  name: string;
  amount: number;
}

export interface PaymentSettingsModel {
  id: string;
  creditCardId?: string;
  gatewayCode?: string;
  method: PaymentMethod;
}

export interface PayerModel {
  fullName: string;
  document: string;
  phone: string;
  email: string;
  address: AddressModel;
}

export interface AddressModel {
  street: string;
  number: string;
  complement?: string;
  neighborhood: string;
  city: string;
  state: string;
  zipCode: string;
}

export enum InvoiceStatus {
  PAID = 'Paid',
  UNPAID = 'Unpaid',
  CANCELED = 'Canceled',
}

export enum PaymentMethod {
  GATEWAY_BALANCE = 'Gateway Balance',
  CREDIT_CARD = 'Credit card',
}
