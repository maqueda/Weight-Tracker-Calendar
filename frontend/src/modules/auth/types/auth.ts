export type AuthUser = {
  id: number;
  username: string;
  displayName: string;
  firstName: string;
  lastName: string;
  email: string | null;
  timezone: string;
};

export type AuthResponse = {
  token: string;
  user: AuthUser;
};

export type ChangePasswordInput = {
  currentPassword: string;
  newPassword: string;
};

export type SimpleMessageResponse = {
  message: string;
};

export type UpdateProfileInput = {
  username: string;
  firstName: string;
  lastName: string;
  email?: string;
};
