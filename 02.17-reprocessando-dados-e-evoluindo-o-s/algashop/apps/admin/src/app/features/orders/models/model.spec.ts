import { OrderFilter, OrderSortProperty } from './model';

describe('OrderFilter', () => {
  it('should serialize sort using sortByProperty and sortDirection', () => {
    const filter = new OrderFilter();
    filter.sort = `${OrderSortProperty.STATUS},DESC`;

    const params = filter.toQueryParams();

    expect(params.get('sortByProperty')).toBe(OrderSortProperty.STATUS);
    expect(params.get('sortDirection')).toBe('DESC');
  });

  it('should not serialize payment method as a filter', () => {
    const filter = new OrderFilter();
    (filter as any).paymentMethod = 'CREDIT_CARD';

    const params = filter.toQueryParams();

    expect(params.has('paymentMethod')).toBeFalse();
  });

  it('should keep customer id support through query params', () => {
    const filter = new OrderFilter();
    filter.customerId = '8b8c38c7-05dc-4f43-8de3-1cdd247f675d';

    const params = filter.toQueryParams();

    expect(params.get('customerId')).toBe('8b8c38c7-05dc-4f43-8de3-1cdd247f675d');
  });
});
