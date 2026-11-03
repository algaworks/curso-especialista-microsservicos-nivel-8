import { AbstractControl, FormControl } from "@angular/forms";

export class AwValidators {

    static noWhitespaceValidator(control: AbstractControl) {
        const isWhitespace = (control.value || '').trim().length === 0;
        const isValid = !isWhitespace;
        return isValid ? null : { 'whitespace': true };
    }
}
