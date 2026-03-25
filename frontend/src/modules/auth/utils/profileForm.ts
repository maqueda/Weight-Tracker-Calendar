import type { AuthUser, UpdateProfileInput } from "../types/auth";

export type ProfileFormState = {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
};

export function buildProfileFormState(user: AuthUser): ProfileFormState {
  return {
    username: user.username,
    firstName: user.firstName || user.displayName,
    lastName: user.lastName || "",
    email: user.email ?? ""
  };
}

export function toUpdateProfileInput(form: ProfileFormState): UpdateProfileInput {
  return {
    username: form.username.trim(),
    firstName: form.firstName.trim(),
    lastName: form.lastName.trim(),
    email: form.email.trim() || undefined
  };
}
