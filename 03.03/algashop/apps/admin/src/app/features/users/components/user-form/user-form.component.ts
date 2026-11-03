import { Component,  Input, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { UsersService } from '../../users.service';
import { firstValueFrom } from 'rxjs';
import { UserInput, UserModel, UserType, UserUpdateInput } from '../../models/model';
import { enumToOptions } from 'src/app/shared/shared.module';

type UserInputControls = { [key: string]: AbstractControl}
type UserInputFormGroup = FormGroup & {value: UserInput, controls: UserInputControls}

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.css'
})
export class UserFormComponent implements OnInit {
  form?: UserInputFormGroup;
  @Input() userId?: string;
  saving = false;
  userTypeOptions: any[] = [];

  constructor(
    private usersService: UsersService,
    private messageService: MessageService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.configureForm();
    this.loadData();
  }

  isEditing() : boolean {
    return !!this.userId
  }

  save() {
    this.saving = true;

    if (this.isEditing()) {
        let input = this.generateUpdateInput();
        firstValueFrom(this.usersService.update(this.userId!, input))
          .then(() => {
            this.saving = false;
            this.displaySuccessMessage();
            this.router.navigate([`/users/${this.userId}`]);
          }).catch((e) => {
            this.saving = false;
            this.displayErrorMessage(e.error);
          })
    } else {
      let input = this.generateInput();
      firstValueFrom(this.usersService.create(input))
        .then((user) => {
          this.saving = false;
          this.displaySuccessMessage();
          this.router.navigate([`/users/${user.id}`]);
        }).catch((e) => {
          this.saving = false;
          this.displayErrorMessage(e.error);
        })
    }

  }

  isFormInvalid() {
    return this.form!.invalid;
  }


  private configureForm() {

    this.userTypeOptions = enumToOptions(UserType);

    this.form = new FormGroup({
      name: new FormControl(null, [Validators.required]),
      email: new FormControl(null, [Validators.required, Validators.email]),
      type: new FormControl(null, [Validators.required]),
      enabled: new FormControl(true, [Validators.required]),
    }) as UserInputFormGroup;

  }

  private loadData() {
    if (this.isEditing()) {
      firstValueFrom(this.usersService.getOne(this.userId!))
        .then((user)=> {
          this.form!.patchValue(this.parsePatchData(user));
        });
    }
  }

  private parsePatchData(user: UserModel) {
    if (user && user.type) {
      user.type = this.userTypeOptions.find(option => option.value === user.type);
    }
    return user;
  }

  private generateInput() : UserInput {
    return new UserInput(
      this.form!.controls.name.value,
      this.form!.controls.email.value,
      this.form!.controls.type.value.value
    );
  }

  private generateUpdateInput() : UserUpdateInput {
    return new UserUpdateInput(
      this.form!.controls.name.value,
      this.form!.controls.type.value.value,
      this.form!.controls.enabled.value
    );
  }

  private displaySuccessMessage() {
    let message = {
      severity:'success',
      summary: 'Success',
      detail:  this.isEditing() ? 'User updated' : 'User registered',
      boolean: true
    }
    this.messageService.add(message);
  }

  private displayErrorMessage(e: Error) {
    let message = {
      severity:'error',
      summary: 'Server connection error, please try again',
      detail: e.message,
      boolean: true
    }
    this.messageService.add(message);
  }
}
