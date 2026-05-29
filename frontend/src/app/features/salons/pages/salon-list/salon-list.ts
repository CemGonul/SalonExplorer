import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { SalonApiService } from '../../../../core/services/salon-api.service';
import { SalonListItem } from '../../models/salon-list-item.model';

@Component({
  selector: 'app-salon-list',
  templateUrl: './salon-list.html',
  styleUrl: './salon-list.scss'
})
export class SalonList implements OnInit {
  private readonly salonApi = inject(SalonApiService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly salons = signal<SalonListItem[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadSalons();
  }

  private loadSalons(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.salonApi.getSalons()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (salons) => {
          this.salons.set(salons);
          this.isLoading.set(false);
        },
        error: () => {
          this.errorMessage.set('Could not load salons. Please make sure the backend is running.');
          this.isLoading.set(false);
        }
      });
  }
}
