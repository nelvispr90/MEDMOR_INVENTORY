import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRegistry, Registry } from '../registry.model';

import { RegistryService } from './registry.service';

describe('Service Tests', () => {
  describe('Registry Service', () => {
    let service: RegistryService;
    let httpMock: HttpTestingController;
    let elemDefault: IRegistry;
    let expectedResult: IRegistry | IRegistry[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(RegistryService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        amount: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Registry', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Registry()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Registry', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            amount: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Registry', () => {
        const patchObject = Object.assign({}, new Registry());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Registry', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            amount: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Registry', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addRegistryToCollectionIfMissing', () => {
        it('should add a Registry to an empty array', () => {
          const registry: IRegistry = { id: 123 };
          expectedResult = service.addRegistryToCollectionIfMissing([], registry);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(registry);
        });

        it('should not add a Registry to an array that contains it', () => {
          const registry: IRegistry = { id: 123 };
          const registryCollection: IRegistry[] = [
            {
              ...registry,
            },
            { id: 456 },
          ];
          expectedResult = service.addRegistryToCollectionIfMissing(registryCollection, registry);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Registry to an array that doesn't contain it", () => {
          const registry: IRegistry = { id: 123 };
          const registryCollection: IRegistry[] = [{ id: 456 }];
          expectedResult = service.addRegistryToCollectionIfMissing(registryCollection, registry);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(registry);
        });

        it('should add only unique Registry to an array', () => {
          const registryArray: IRegistry[] = [{ id: 123 }, { id: 456 }, { id: 39793 }];
          const registryCollection: IRegistry[] = [{ id: 123 }];
          expectedResult = service.addRegistryToCollectionIfMissing(registryCollection, ...registryArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const registry: IRegistry = { id: 123 };
          const registry2: IRegistry = { id: 456 };
          expectedResult = service.addRegistryToCollectionIfMissing([], registry, registry2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(registry);
          expect(expectedResult).toContain(registry2);
        });

        it('should accept null and undefined values', () => {
          const registry: IRegistry = { id: 123 };
          expectedResult = service.addRegistryToCollectionIfMissing([], null, registry, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(registry);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
