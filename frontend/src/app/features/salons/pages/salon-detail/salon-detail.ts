import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { SalonApiService } from '../../../../core/services/salon-api.service';
import { SalonDetail } from '../../models/salon-detail.model';

@Component({
  selector: 'app-salon-detail',
  imports: [RouterLink],
  templateUrl: './salon-detail.html',
  styleUrl: './salon-detail.scss'
})
export class SalonDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly salonApi = inject(SalonApiService);

  protected readonly salon = signal<SalonDetail | null>(null);
  protected readonly isLoading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (Number.isNaN(id) || id <= 0) {
      this.errorMessage.set('Invalid salon id.');
      this.isLoading.set(false);
      return;
    }

    this.salonApi.getSalonById(id).subscribe({
      next: (salon) => {
        this.salon.set(salon);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load salon details.');
        this.isLoading.set(false);
      }
    });
  }
}
