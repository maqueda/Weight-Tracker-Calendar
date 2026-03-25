<template>
  <section class="password-card">
    <div class="password-head">
      <div>
        <p class="password-label">Seguridad</p>
        <h3>Cambiar contraseña</h3>
      </div>
    </div>

    <p v-if="successMessage" class="success">{{ successMessage }}</p>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

    <form class="password-form" @submit.prevent="handleSubmit">
      <label>
        <span>Contraseña actual</span>
        <input
          v-model="currentPassword"
          type="password"
          autocomplete="current-password"
          required
        />
      </label>
      <label>
        <span>Nueva contraseña</span>
        <input
          v-model="newPassword"
          type="password"
          autocomplete="new-password"
          required
        />
      </label>
      <label>
        <span>Repetir nueva contraseña</span>
        <input
          v-model="confirmPassword"
          type="password"
          autocomplete="new-password"
          required
        />
      </label>
      <button :disabled="authStore.loading" type="submit">Actualizar contraseña</button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuthStore } from "../store/useAuthStore";

const authStore = useAuthStore();
const currentPassword = ref("");
const newPassword = ref("");
const confirmPassword = ref("");
const errorMessage = ref("");
const successMessage = ref("");

async function handleSubmit() {
  errorMessage.value = "";
  successMessage.value = "";

  if (newPassword.value !== confirmPassword.value) {
    errorMessage.value = "La confirmación de la nueva contraseña no coincide.";
    return;
  }

  try {
    const response = await authStore.changePassword({
      currentPassword: currentPassword.value,
      newPassword: newPassword.value
    });
    successMessage.value = response.message;
    currentPassword.value = "";
    newPassword.value = "";
    confirmPassword.value = "";
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : "No se pudo cambiar la contraseña.";
  }
}
</script>

<style scoped>
.password-card {
  padding: 18px 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid #d9e5f2;
}

.password-head h3 {
  margin: 6px 0 0;
  color: #18304c;
}

.password-label {
  margin: 0;
  color: #5f7895;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.72rem;
}

.password-form {
  display: grid;
  gap: 12px;
  margin-top: 14px;
}

.password-form label {
  display: grid;
  gap: 6px;
  color: #35506d;
  font-weight: 600;
}

.password-form input {
  border: 1px solid #d3dfec;
  border-radius: 12px;
  padding: 10px 12px;
}

.password-form button {
  border: 0;
  border-radius: 12px;
  padding: 12px 14px;
  background: #132238;
  color: #f5f9ff;
  font-weight: 700;
  cursor: pointer;
}

.password-form button:disabled {
  opacity: 0.7;
  cursor: wait;
}

.error,
.success {
  margin: 12px 0 0;
  padding: 10px 12px;
  border-radius: 12px;
}

.error {
  background: #ffe6e2;
  color: #8a2f25;
}

.success {
  background: #edf8f3;
  color: #1f5e3b;
}
</style>
