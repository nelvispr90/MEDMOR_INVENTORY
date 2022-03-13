import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IProduct, Product } from '../product.model';
import { ProductService } from '../service/product.service';

@Component({
  selector: 'jhi-product-update',
  templateUrl: './product-update.component.html',
})
export class ProductUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    productSize: [null, [Validators.required]],
    color: [null, [Validators.required]],
    price: [null, [Validators.required]],
    fragile: [null, [Validators.required]],
    lote: [null, [Validators.required, Validators.pattern('^[a-zA-Z0-9_]*$')]],
    containerType: [null, [Validators.required]],
  });

  constructor(protected productService: ProductService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ product }) => {
      this.updateForm(product);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const product = this.createFromForm();
    if (product.id !== undefined) {
      this.subscribeToSaveResponse(this.productService.update(product));
    } else {
      this.subscribeToSaveResponse(this.productService.create(product));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduct>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(product: IProduct): void {
    this.editForm.patchValue({
      id: product.id,
      productSize: product.productSize,
      color: product.color,
      price: product.price,
      fragile: product.fragile,
      lote: product.lote,
      containerType: product.containerType,
    });
  }

  protected createFromForm(): IProduct {
    return {
      ...new Product(),
      id: this.editForm.get(['id'])!.value,
      productSize: this.editForm.get(['productSize'])!.value,
      color: this.editForm.get(['color'])!.value,
      price: this.editForm.get(['price'])!.value,
      fragile: this.editForm.get(['fragile'])!.value,
      lote: this.editForm.get(['lote'])!.value,
      containerType: this.editForm.get(['containerType'])!.value,
    };
  }
}
