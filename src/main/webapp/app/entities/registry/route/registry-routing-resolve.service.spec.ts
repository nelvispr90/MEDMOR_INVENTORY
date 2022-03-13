jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IRegistry, Registry } from '../registry.model';
import { RegistryService } from '../service/registry.service';

import { RegistryRoutingResolveService } from './registry-routing-resolve.service';

describe('Service Tests', () => {
  describe('Registry routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: RegistryRoutingResolveService;
    let service: RegistryService;
    let resultRegistry: IRegistry | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(RegistryRoutingResolveService);
      service = TestBed.inject(RegistryService);
      resultRegistry = undefined;
    });

    describe('resolve', () => {
      it('should return IRegistry returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRegistry = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultRegistry).toEqual({ id: 123 });
      });

      it('should return new IRegistry if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRegistry = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultRegistry).toEqual(new Registry());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultRegistry = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultRegistry).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
