import { defineStore } from "pinia";
import { router } from "../../../app/router";
import { changePassword, getCurrentUser, login, register, updateProfile } from "../services/authApi";
import { clearAuthToken, getAuthToken, setAuthToken } from "../services/authStorage";
import type { AuthUser, ChangePasswordInput, UpdateProfileInput } from "../types/auth";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: getAuthToken(),
    user: null as AuthUser | null,
    loading: false,
    initialized: false
  }),
  getters: {
    isAuthenticated(state) {
      return Boolean(state.token);
    }
  },
  actions: {
    async restoreSession() {
      if (!this.token) {
        this.initialized = true;
        return;
      }

      this.loading = true;
      try {
        this.user = await getCurrentUser();
      } catch {
        this.logout(false);
      } finally {
        this.loading = false;
        this.initialized = true;
      }
    },
    async login(input: { username: string; password: string }) {
      this.loading = true;
      try {
        const response = await login(input);
        this.token = response.token;
        this.user = response.user;
        setAuthToken(response.token);
        await router.push("/");
      } finally {
        this.loading = false;
      }
    },
    async register(input: { username: string; displayName: string; email?: string; password: string }) {
      this.loading = true;
      try {
        const response = await register(input);
        this.token = response.token;
        this.user = response.user;
        setAuthToken(response.token);
        await router.push("/");
      } finally {
        this.loading = false;
      }
    },
    async changePassword(input: ChangePasswordInput) {
      this.loading = true;
      try {
        return await changePassword(input);
      } finally {
        this.loading = false;
      }
    },
    async updateProfile(input: UpdateProfileInput) {
      this.loading = true;
      try {
        const user = await updateProfile(input);
        this.user = user;
        return user;
      } finally {
        this.loading = false;
      }
    },
    logout(redirect = true) {
      this.token = null;
      this.user = null;
      clearAuthToken();
      if (redirect) {
        void router.push("/auth");
      }
    }
  }
});
