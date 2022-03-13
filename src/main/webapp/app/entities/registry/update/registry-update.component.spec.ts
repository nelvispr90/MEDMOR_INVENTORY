jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { RegistryService } from '../service/registry.service';
import { IRegistry, Registry } from '../registry.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ISection } from 'app/entities/section/section.model';
import { SectionService } from 'app/entities/section/service/section.service';

import { RegistryUpdateComponent } from './registry-update.component';

describe('Component Tests', () => {
  describe('Registry Management Update Component', () => {
    let comp: RegistryUpdateComponent;
    let fixture: ComponentFixture<RegistryUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let registryService: RegistryService;
    let productService: ProductService;
    let sectionService: SectionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RegistryUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(RegistryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RegistryUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      registryService = TestBed.inject(RegistryService);
      productService = TestBed.inject(ProductService);
      sectionService = TestBed.inject(SectionService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Product query and add missing value', () => {
        const registry: IRegistry = { id: 456 };
        const product: IProduct = { id: 48656 };
        registry.product = product;

        const productCollection: IProduct[] = [{ id: 58239 }];
        spyOn(productService, 'query').and.returnValue(of(new HttpResponse({ body: productCollection })));
        const additionalProducts = [product];
        const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
        spyOn(productService, 'addProductToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ registry });
        comp.ngOnInit();

        expect(productService.query).toHaveBeenCalled();
        expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(productCollection, ...additionalProducts);
        expect(comp.productsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Section query and add missing value', () => {
        const registry: IRegistry = { id: 456 };
        const section: ISection = { id: 69005 };
        registry.section = section;

        const sectionCollection: ISection[] = [{ id: 93284 }];
        spyOn(sectionService, 'query').and.returnValue(of(new HttpResponse({ body: sectionCollection })));
        const additionalSections = [section];
        const expectedCollection: ISection[] = [...additionalSections, ...sectionCollection];
        spyOn(sectionService, 'addSectionToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ registry });
        comp.ngOnInit();

        expect(sectionService.query).toHaveBeenCalled();
        expect(sectionService.addSectionToCollectionIfMissing).toHaveBeenCalledWith(sectionCollection, ...additionalSections);
        expect(comp.sectionsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const registry: IRegistry = { id: 456 };
        const product: IProduct = { id: 7015 };
        registry.product = product;
        const section: ISection = { id: 65842 };
        registry.section = section;

        activatedRoute.data = of({ registry });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(registry));
        expect(comp.productsSharedCollection).toContain(product);
        expect(comp.sectionsSharedCollection).toContain(section);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const registry = { id: 123 };
        spyOn(registryService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ registry });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: registry }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(registryService.update).toHaveBeenCalledWith(registry);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const registry = new Registry();
        spyOn(registryService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ registry });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: registry }));
        saveSubject.complete();

        // THEN
        expect(registryService.create).toHaveBeenCalledWith(registry);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const registry = { id: 123 };
        spyOn(registryService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ registry });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(registryService.update).toHaveBeenCalledWith(registry);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackProductById', () => {
        it('Should return tracked Product primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackProductById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackSectionById', () => {
        it('Should return tracked Section primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSectionById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
