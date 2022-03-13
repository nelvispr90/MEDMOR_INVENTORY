import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RegistryDetailComponent } from './registry-detail.component';

describe('Component Tests', () => {
  describe('Registry Management Detail Component', () => {
    let comp: RegistryDetailComponent;
    let fixture: ComponentFixture<RegistryDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [RegistryDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ registry: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(RegistryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RegistryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load registry on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.registry).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
