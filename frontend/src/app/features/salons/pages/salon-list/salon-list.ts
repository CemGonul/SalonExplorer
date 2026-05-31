import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { SalonApiService } from '../../../../core/services/salon-api.service';
import { SalonListItem } from '../../models/salon-list-item.model';

@Component({
  selector: 'app-salon-list',
  imports: [RouterLink],
  templateUrl: './salon-list.html',
  styleUrl: './salon-list.scss'
})
export class SalonList implements OnInit {
  private readonly salonApi = inject(SalonApiService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly districts = signal<string[]>([]);
  protected readonly selectedDistrict = signal('');
  protected readonly selectedSortBy = signal('');
  protected readonly selectedDirection = signal('');
  protected readonly salons = signal<SalonListItem[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadDistricts();
    this.loadSalons();
  }

  protected onDistrictChange(district: string): void {
    this.selectedDistrict.set(district);
    this.loadSalons();
  }

  protected onSortByChange(sortBy: string): void {
    this.selectedSortBy.set(sortBy);
    this.selectedDirection.set(this.defaultDirectionFor(sortBy));
    this.loadSalons();
  }

  protected onDirectionChange(direction: string): void {
    this.selectedDirection.set(direction);
    this.loadSalons();
  }

  private defaultDirectionFor(sortBy: string): string {
    if (!sortBy) {
      return '';
    }

    if (sortBy === 'name') {
      return 'asc';
    }

    return 'desc';
  }

  private loadDistricts(): void {
    this.salonApi.getDistricts()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (districts) => {
          this.districts.set(districts);
        },
        error: () => {
          this.districts.set([]);
        }
      });
  }

  private loadSalons(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.salonApi.getSalons({
      district: this.selectedDistrict() || undefined,
      sortBy: this.selectedSortBy() || undefined,
      direction: this.selectedSortBy() && this.selectedDirection()
        ? this.selectedDirection()
        : undefined
    })
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
