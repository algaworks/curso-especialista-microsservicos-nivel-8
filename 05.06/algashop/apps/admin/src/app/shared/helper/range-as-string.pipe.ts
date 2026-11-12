import { Pipe, PipeTransform } from '@angular/core';
import { locale } from 'src/app/app.module';

@Pipe({
  name: 'rangeAsString'
})
export class RangeAsStringPipe implements PipeTransform {


  transform(dataRange?: Date[]): string {
    if (!dataRange) {
      return '';
    }

    if (dataRange && dataRange.length == 0) {
      return '';
    }

    let to = undefined
    let from = undefined

    if (dataRange.length > 0 && dataRange[0]) {
      from = dataRange[0];
    }

    if (dataRange.length > 1 && dataRange[1]) {
      to = dataRange[1];
    }

    if (to && from) {
      return `de ${from.toLocaleDateString(locale)} até ${to.toLocaleDateString(locale)}`;
    }

    if (to) {
      return `até ${to.toLocaleDateString(locale)}`;
    }

    if (from) {
      return `a partir de ${from.toLocaleDateString(locale)}`;
    }

    return '';
  }

}
 