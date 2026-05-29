export interface SalonListItem {
  id: number;
  name: string;
  district: string;
  rating: number | null;
  reviewCount: number | null;
  priceRange: string | null;
}
