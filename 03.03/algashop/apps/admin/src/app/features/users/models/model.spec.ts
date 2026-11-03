import { UserFilter, UserSortProperty } from './model';

describe('UserFilter', () => {
  it('should serialize sort using sortByProperty and sortDirection', () => {
    const filter = new UserFilter();
    filter.sort = `${UserSortProperty.EMAIL},DESC`;

    const params = filter.toQueryParams();

    expect(params.get('sortByProperty')).toBe(UserSortProperty.EMAIL);
    expect(params.get('sortDirection')).toBe('DESC');
    expect(params.has('sort')).toBeFalse();
    expect(params.has('direction')).toBeFalse();
  });

  it('should serialize type autocomplete option as enum value', () => {
    const filter = new UserFilter();
    filter.type = { label: 'Manager', value: 'MANAGER' };

    const params = filter.toQueryParams();

    expect(params.get('type')).toBe('MANAGER');
  });

  it('should serialize type value directly', () => {
    const filter = new UserFilter();
    filter.type = 'OPERATOR';

    const params = filter.toQueryParams();

    expect(params.get('type')).toBe('OPERATOR');
  });
});
