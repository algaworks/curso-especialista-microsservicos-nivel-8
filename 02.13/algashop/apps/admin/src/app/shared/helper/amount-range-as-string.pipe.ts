import { Pipe, PipeTransform } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

@Pipe({
  name: 'amountRangeAsString'
})
export class AmountRangeAsStringPipe implements PipeTransform {


  currencyPipe = new CurrencyPipe('en', 'USD')

  transform(from?: number, to?: number): string {
    if (!from && !to) {
      return '';
    }

    if (to && from) {
      return `from $ ${this.currencyPipe.transform(from)} to $ ${this.currencyPipe.transform(to)}`;
    }

    if (to) {
      return `from $ ${this.currencyPipe.transform(to)}`;
    }

    if (from) {
      return `starting from $ ${this.currencyPipe.transform(from)}`;
    }

    return '';
  }

}
