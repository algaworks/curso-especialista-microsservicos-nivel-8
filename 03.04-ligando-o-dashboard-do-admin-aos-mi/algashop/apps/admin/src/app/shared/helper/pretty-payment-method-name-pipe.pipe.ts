import { Pipe, PipeTransform } from '@angular/core';

import { TitleCasePipe } from '@angular/common'

@Pipe({
  name: 'prettyPaymentMethodName'
})
export class PrettyPaymentMethodNamePipePipe implements PipeTransform {

  private titleCasePipe = new TitleCasePipe()

  transform(value?: string): string {
    if (!value) {
      return '';
    }

    if (value.toUpperCase() == 'PAGSEGURO') {
      return 'PagSeguro'
    }

    return this.titleCasePipe.transform(value);
  }

}
