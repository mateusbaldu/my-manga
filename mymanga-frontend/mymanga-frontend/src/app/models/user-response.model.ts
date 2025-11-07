export interface UserResponse {
  name: string;
  username: string;
  createdAt: string;
  roles: { id: number, name: string }[];
}
