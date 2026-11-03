import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { CategoriesService } from '../../categories.service';
import { firstValueFrom } from 'rxjs';
import { CategoryInput, CategoryModel } from '../../models/models';

type CategoryInputControls = { [key in keyof CategoryInput]: AbstractControl}
type CategoryInputFormGroup = FormGroup & {value: CategoryInput, control: CategoryInputControls}

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrl: './category-form.component.css'
})
export class CategoryFormComponent implements OnInit {
  form?: CategoryInputFormGroup;
  @Input() categoryId?: string;
  saving = false;

  constructor(
    private categoriesService: CategoriesService,
    private messageService: MessageService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.configureForm();
    this.loadData();
  }

  isEditing() : boolean {
    return !!this.categoryId
  }

  save() {
    this.saving = true;

    let input = this.generateInput();

    if (this.isEditing()) {
        firstValueFrom(this.categoriesService.update(this.categoryId!, input))
          .then(() => {
            this.saving = false;
            this.displaySuccessMessage();
            this.router.navigate([`/categories/${this.categoryId}`]);
          }).catch((e) => {
            console.error(e);
            this.saving = false;
            this.displayErrorMessage(e);
          })
    } else {
      firstValueFrom(this.categoriesService.create(input))
        .then((category) => {
          this.saving = false;
          this.displaySuccessMessage();
          this.router.navigate([`/categories/${category.id}`]);
        }).catch((e) => {
          console.error(e);
          this.saving = false;
          this.displayErrorMessage(e);
        })
    }

  }

  isFormInvalid() {
    return this.form!.invalid;
  }

  private configureForm() {
    this.form = new FormGroup({
      name: new FormControl(null, [Validators.required]),
      enabled: new FormControl(false, [Validators.required]),
    }) as CategoryInputFormGroup;

  }

  private loadData() {
    if (this.isEditing()) {
      firstValueFrom(this.categoriesService.getOne(this.categoryId!))
        .then((category)=> {
          this.form!.patchValue(this.parsePatchData(category));
        });
    }
  }

  private parsePatchData(category: CategoryModel) {
    return category;
  }

  private generateInput() : CategoryInput {
    return new CategoryInput(
      this.form!.controls.name.value,
      this.form!.controls.enabled.value
    );
  }

  private displaySuccessMessage() {
    let message = {
      severity:'success',
      summary: 'Success',
      detail:  this.isEditing() ? 'Category updated' : 'Category registered',
      boolean: true
    }
    this.messageService.add(message);
  }

  private displayErrorMessage(e: Error) {
    let message = {
      severity:'error',
      summary: 'Connection error with the server, please try again',
      detail: e.message,
      boolean: true
    }
    this.messageService.add(message);
  }
}
