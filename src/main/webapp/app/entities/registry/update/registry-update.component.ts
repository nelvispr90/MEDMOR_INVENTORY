import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRegistry, Registry } from '../registry.model';
import { RegistryService } from '../service/registry.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ISection } from 'app/entities/section/section.model';
import { SectionService } from 'app/entities/section/service/section.service';

@Component({
  selector: 'jhi-registry-update',
  templateUrl: './registry-update.component.html',
})
export class RegistryUpdateComponent implements OnInit {
  isSaving = false;

  productsSharedCollection: IProduct[] = [];
  sectionsSharedCollection: ISection[] = [];

  editForm = this.fb.group({
    id: [],
    amount: [null, [Validators.required]],
    product: [null, Validators.required],
    section: [null, Validators.required],
  });

  constructor(
    protected registryService: RegistryService,
    protected productService: ProductService,
    protected sectionService: SectionService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ registry }) => {
      this.updateForm(registry);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const registry = this.createFromForm();
    if (registry.id !== undefined) {
      this.subscribeToSaveResponse(this.registryService.update(registry));
    } else {
      this.subscribeToSaveResponse(this.registryService.create(registry));
    }
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  trackSectionById(index: number, item: ISection): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRegistry>>): void {
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

  protected updateForm(registry: IRegistry): void {
    this.editForm.patchValue({
      id: registry.id,
      amount: registry.amount,
      product: registry.product,
      section: registry.section,
    });

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(this.productsSharedCollection, registry.product);
    this.sectionsSharedCollection = this.sectionService.addSectionToCollectionIfMissing(this.sectionsSharedCollection, registry.section);
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing(products, this.editForm.get('product')!.value))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.sectionService
      .query()
      .pipe(map((res: HttpResponse<ISection[]>) => res.body ?? []))
      .pipe(
        map((sections: ISection[]) => this.sectionService.addSectionToCollectionIfMissing(sections, this.editForm.get('section')!.value))
      )
      .subscribe((sections: ISection[]) => (this.sectionsSharedCollection = sections));
  }

  protected createFromForm(): IRegistry {
    return {
      ...new Registry(),
      id: this.editForm.get(['id'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      product: this.editForm.get(['product'])!.value,
      section: this.editForm.get(['section'])!.value,
    };
  }
}
