jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SectionService } from '../service/section.service';
import { ISection, Section } from '../section.model';

import { SectionUpdateComponent } from './section-update.component';

describe('Component Tests', () => {
  describe('Section Management Update Component', () => {
    let comp: SectionUpdateComponent;
    let fixture: ComponentFixture<SectionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let sectionService: SectionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SectionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SectionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SectionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      sectionService = TestBed.inject(SectionService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const section: ISection = { id: 456 };

        activatedRoute.data = of({ section });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(section));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const section = { id: 123 };
        spyOn(sectionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ section });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: section }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(sectionService.update).toHaveBeenCalledWith(section);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const section = new Section();
        spyOn(sectionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ section });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: section }));
        saveSubject.complete();

        // THEN
        expect(sectionService.create).toHaveBeenCalledWith(section);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const section = { id: 123 };
        spyOn(sectionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ section });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(sectionService.update).toHaveBeenCalledWith(section);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
