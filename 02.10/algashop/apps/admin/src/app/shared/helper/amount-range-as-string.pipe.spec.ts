import { AmountRangeAsStringPipe } from './amount-range-as-string.pipe';

describe('AmountRangeAsStringPipe', () => {
  it('create an instance', () => {
    const pipe = new AmountRangeAsStringPipe();
    expect(pipe).toBeTruthy();
  });
});
