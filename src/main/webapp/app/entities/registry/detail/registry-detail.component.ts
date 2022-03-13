import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRegistry } from '../registry.model';

@Component({
  selector: 'jhi-registry-detail',
  templateUrl: './registry-detail.component.html',
})
export class RegistryDetailComponent implements OnInit {
  registry: IRegistry | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ registry }) => {
      this.registry = registry;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
