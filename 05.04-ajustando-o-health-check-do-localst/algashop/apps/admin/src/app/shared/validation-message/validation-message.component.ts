import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';

@Component({
  selector: 'app-validation-message',
  templateUrl: './validation-message.component.html',
  styleUrls: ['./validation-message.component.css']
})
export class ValidationMessageComponent {

  @Input() error?: string;
  @Input() control?: AbstractControl | FormControl | null;
  @Input() text?: string;

  hasError(): boolean {
    if (!this.control) {
      return false;
    }
    return this.control.hasError(this.error!) && this.control.dirty;
  }

}
