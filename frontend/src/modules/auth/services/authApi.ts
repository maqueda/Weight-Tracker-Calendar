import { http } from "../../../shared/services/http";
import type {
  AuthResponse,
  AuthUser,
  ChangePasswordInput,
  SimpleMessageResponse,
  UpdateProfileInput
} from "../types/auth";

export function login(input: { username: string; password: string }) {
  return http<AuthResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify(input)
  });
}

export function register(input: { username: string; displayName: string; email?: string; password: string }) {
  return http<AuthResponse>("/auth/register", {
    method: "POST",
    body: JSON.stringify(input)
  });
}

export function getCurrentUser() {
  return http<AuthUser>("/auth/me");
}

export function changePassword(input: ChangePasswordInput) {
  return http<SimpleMessageResponse>("/auth/change-password", {
    method: "POST",
    body: JSON.stringify(input)
  });
}

export function updateProfile(input: UpdateProfileInput) {
  return http<AuthUser>("/auth/profile", {
    method: "PUT",
    body: JSON.stringify(input)
  });
}
